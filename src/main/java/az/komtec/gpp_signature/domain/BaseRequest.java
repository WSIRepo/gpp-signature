package az.komtec.gpp_signature.domain;

import lombok.Data;

@Data
public class BaseRequest
{
    private String keystorePath;
    private String keystorePassword;
    private String keyAlias;
    private String gppJson = "{\"code\":\"SUCCESS_FINAL\",\"errorDetail\":null,\"session\":\"8eb4dd04-2024-4b52-8582-6b68bc68ce34\",\"title\":null,\"help\":null,\"fields\":null,\"dialogs\":null,\"customer\":null,\"payment\":{\"options\":null,\"products\":[{\"id\":{\"title\":\"Номер телефона\",\"value\":\"556285925\"},\"description\":{\"title\":\"Тип тарифа\",\"value\":\"Постоплата\"},\"additionalInfo\":null,\"purchaseSum\":null,\"minSum\":100,\"maxSum\":20000,\"feeSum\":null,\"allowedSum\":null}],\"currency\":\"AZN\"},\"action\":{\"prev\":\"SELECT_PAYMENT_ITEM\",\"current\":\"SHOW_PAYMENT_ITEM\",\"next\":\"PAY\"}}";
}
