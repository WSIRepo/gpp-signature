package my.test.gpp_signature.util;

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

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;


@Slf4j
public class Utils {

    private static final String keyStorePath = "/Users/huseyn.pashayev/desktop/gpp.jks";
    private static final String password = "Kap2019!?";
    private static final String keyAlias = "gppKey";
    private static final String algorithm = "SHA1WithRSA";

    public static KeyStore getKeyStore() {
        KeyStore keyStore = null;
        try {
            FileInputStream is = new FileInputStream(keyStorePath);
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(is, password.toCharArray());
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return keyStore;
    }

    public static CMSSignedDataGenerator getCMSDataGenerator(KeyStore keystore) throws Exception {

        CMSSignedDataGenerator cmsSignedDataGenerator = null;
        try {
            Security.addProvider(new BouncyCastleProvider());

            Certificate[] certChain = keystore.getCertificateChain(keyAlias);

            Store certStore = new JcaCertStore(Arrays.asList(certChain));

            Certificate cert = keystore.getCertificate(keyAlias);

            ContentSigner signer = new JcaContentSignerBuilder(algorithm).setProvider("BC").
                    build((PrivateKey) (keystore.getKey(keyAlias, password.toCharArray())));

            cmsSignedDataGenerator = new CMSSignedDataGenerator();

            cmsSignedDataGenerator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").
                    build()).build(signer, (X509Certificate) cert));

            cmsSignedDataGenerator.addCertificates(certStore);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return cmsSignedDataGenerator;
    }

    public static byte[] getPKCS7Signature(byte[] content, final CMSSignedDataGenerator generator) throws Exception {

        CMSTypedData cmsData = new CMSProcessableByteArray(content);
        CMSSignedData signedData = generator.generate(cmsData, true);
        return signedData.getEncoded();
    }


}