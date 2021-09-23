package az.komtec.gpp_signature.domain;

import lombok.Data;

@Data
public class BaseResponse {
    private String status;
    private String signature;
}
