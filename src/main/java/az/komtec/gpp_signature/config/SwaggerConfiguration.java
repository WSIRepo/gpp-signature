package az.komtec.gpp_signature.config;

import com.fasterxml.classmate.TypeResolver;
import az.komtec.gpp_signature.config.properties.ApplicationDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {


    @Bean
    public Docket docket(TypeResolver typeResolver) {
        Contact contact = new Contact(
                ApplicationDefaults.Swagger.contactName,
                ApplicationDefaults.Swagger.contactUrl,
                ApplicationDefaults.Swagger.contactEmail
        );

        ApiInfo apiInfo = new ApiInfo(
                ApplicationDefaults.Swagger.title,
                ApplicationDefaults.Swagger.description,
        ApplicationDefaults.Swagger.version,
                ApplicationDefaults.Swagger.termsOfServiceUrl,
                contact,
                ApplicationDefaults.Swagger.license,
                ApplicationDefaults.Swagger.licenseUrl,
                new ArrayList<>()
        );

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors
                        .regex(ApplicationDefaults.Swagger.defaultIncludePattern))
                .apis(RequestHandlerSelectors
                        .basePackage("az.komtec.gpp_signature"))
                .build()
                .apiInfo(apiInfo)
                .host(ApplicationDefaults.Swagger.host)
                .protocols(new HashSet<>(Arrays.asList(ApplicationDefaults.Swagger.protocols)))
                .produces(Collections.singleton(MediaType.APPLICATION_JSON_VALUE))
                .consumes(Collections.singleton(MediaType.APPLICATION_JSON_VALUE));
                //.globalResponseMessage(RequestMethod.GET, getCustomizedResponseMessages())
                //.globalResponseMessage(RequestMethod.PUT, getCustomizedResponseMessages())
                //.globalResponseMessage(RequestMethod.DELETE, getCustomizedResponseMessages())
                //.globalResponseMessage(RequestMethod.POST, getCustomizedResponseMessages())
    }

    private List<ResponseMessage> getCustomizedResponseMessages() {

        final ModelRef errorResponse = new ModelRef("RestErrorResponse");

        List<ResponseMessage> responseMessages = new ArrayList<>();
        responseMessages.add(new ResponseMessageBuilder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .responseModel(errorResponse)
                .build());

        responseMessages.add(new ResponseMessageBuilder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .build());

        responseMessages.add(new ResponseMessageBuilder()
                .code(HttpStatus.FORBIDDEN.value())
                .message(HttpStatus.FORBIDDEN.getReasonPhrase())
                .build());

        responseMessages.add(new ResponseMessageBuilder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(HttpStatus.NOT_FOUND.getReasonPhrase())
                .build());

        responseMessages.add(new ResponseMessageBuilder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .responseModel(errorResponse)
                .build());

        return responseMessages;
    }
}
