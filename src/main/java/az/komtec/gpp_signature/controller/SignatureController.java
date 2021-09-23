package az.komtec.gpp_signature.controller;

import az.komtec.gpp_signature.domain.BaseRequest;
import az.komtec.gpp_signature.domain.BaseResponse;
import az.komtec.gpp_signature.util.CryptographicUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyStore;
import java.util.Base64;

@RestController
@RequestMapping("/signature")
@Slf4j
@RequiredArgsConstructor
public class SignatureController {
    private final CryptographicUtils cryptographicUtils;

    @PostMapping
    public BaseResponse getSignature(
            BaseRequest request) {
        log.info(String.format("Sign request - %s", request.getGppJson()));

        BaseResponse response = new BaseResponse();
        response.setStatus("FAILED");
        try {

            //Remove any spaces
            String gppJson = request.getGppJson().replaceAll("\\s", "");

            KeyStore keyStore = cryptographicUtils.getKeyStore(request.getKeystorePath(),
                    request.getKeystorePassword());

            if (keyStore == null) {
                throw new Exception("Keystore not found");
            }

            CMSSignedDataGenerator cmsSignedDataGenerator = cryptographicUtils.getCMSDataGenerator(keyStore,
                    request.getKeyAlias(),
                    request.getKeystorePassword());

            if (cmsSignedDataGenerator == null) {
                throw new Exception("CMS signed data generator is null");
            }

            log.debug("Signing...");
            byte[] pkcs7SignedData = cryptographicUtils.getPKCS7Signature(gppJson.getBytes(), cmsSignedDataGenerator);

            log.debug("Signed");

            response.setSignature(new String(Base64.getEncoder().encode(pkcs7SignedData)));
            response.setStatus("SIGNED");
            log.debug(response.toString());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return response;

    }

}
