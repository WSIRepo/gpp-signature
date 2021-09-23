package my.test.gpp_signature.controller;

import lombok.extern.slf4j.Slf4j;
import my.test.gpp_signature.util.Utils;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyStore;
import java.util.Base64;

@RestController
@RequestMapping("/signature")
@Slf4j
public class GppSignature {
    @GetMapping
    public String getSignature(@RequestParam("json") String json) {
        String result = "NoSignature";
        try {

            KeyStore keyStore = Utils.getKeyStore();

            if (keyStore == null) {
                throw new Exception("Keystore not found");
            }

            log.debug("Keystore loaded");

            CMSSignedDataGenerator cmsSignedDataGenerator = Utils.getCMSDataGenerator(keyStore);

            if (cmsSignedDataGenerator == null) {
                throw new Exception("CMS signed data generator is null");
            }

            byte[] pkcs7SignedData = Utils.getPKCS7Signature(json.getBytes(), cmsSignedDataGenerator);

            log.debug("Signed");

            result = new String(Base64.getEncoder().encode(pkcs7SignedData));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return result;

    }

}
