package az.komtec.gpp_signature.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;


@Slf4j
@Component
@RequiredArgsConstructor
public class CryptographicUtils {

    private final String algorithm = "SHA1WithRSA";

    public KeyStore getKeyStore(String path,String password) {
        KeyStore keyStore = null;
        try {
            log.debug(String.format("Loading keystore %s",path));
            FileInputStream is = new FileInputStream(path);
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(is, password.toCharArray());
            log.debug("Keystore loaded");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return keyStore;
    }

    public CMSSignedDataGenerator getCMSDataGenerator(KeyStore keystore,
                                                      String alias,
                                                      String password) throws Exception {

        CMSSignedDataGenerator cmsSignedDataGenerator = null;
        try {
            Security.addProvider(new BouncyCastleProvider());

            log.debug(String.format("Getting %s chain and key from keystore",alias));

            Certificate[] certChain = keystore.getCertificateChain(alias);

            log.debug("Chain obtained");
            Store certStore = new JcaCertStore(Arrays.asList(certChain));

            Certificate cert = keystore.getCertificate(alias);

            log.debug("Key obtained");
            ContentSigner signer = new JcaContentSignerBuilder(algorithm).setProvider("BC").
                    build((PrivateKey) (keystore.getKey(alias, password.toCharArray())));

            cmsSignedDataGenerator = new CMSSignedDataGenerator();

            cmsSignedDataGenerator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").
                    build()).build(signer, (X509Certificate) cert));

            cmsSignedDataGenerator.addCertificates(certStore);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return cmsSignedDataGenerator;
    }

    public byte[] getPKCS7Signature(byte[] content, final CMSSignedDataGenerator generator) throws Exception {

        CMSTypedData cmsData = new CMSProcessableByteArray(content);
        CMSSignedData signedData = generator.generate(cmsData, true);
        return signedData.getEncoded();
    }


}
