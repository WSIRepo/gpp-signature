package my.test.gpp_signature.controller;

import lombok.extern.slf4j.Slf4j;
import my.test.gpp_signature.domain.BaseResponse;
import my.test.gpp_signature.util.Utils;
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
public class GppSignature {
    @PostMapping
    public BaseResponse getSignature(@RequestBody String rawJson) {
        log.info(String.format("Sign request - %s", rawJson));
        BaseResponse response = new BaseResponse();
        response.setStatus("FAILED");
        try {

            KeyStore keyStore = Utils.getKeyStore();

            if (keyStore == null) {
                throw new Exception("Keystore not found");
            }

            CMSSignedDataGenerator cmsSignedDataGenerator = Utils.getCMSDataGenerator(keyStore);

            if (cmsSignedDataGenerator == null) {
                throw new Exception("CMS signed data generator is null");
            }

            log.debug("Signing...");
            byte[] pkcs7SignedData = Utils.getPKCS7Signature(rawJson.getBytes(), cmsSignedDataGenerator);

            log.debug("Signed");

            response.setSignature(new String(Base64.getEncoder().encode(pkcs7SignedData)));
            response.setStatus("DONE");
            log.debug(response.toString());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return response;

    }

}
