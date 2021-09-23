package my.test.gpp_signature.domain;

import lombok.Data;

@Data
public class BaseResponse {
    private String status;
    private String signature;
}
