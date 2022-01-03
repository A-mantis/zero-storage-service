package com.zero.storage.config;


import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;

@Configuration
public class SwaggerConfig {

    private String env;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .enable(!"PROD".equalsIgnoreCase(env))
            .apiInfo(apiInfo())
            .securitySchemes(securitySchemes())
            .securityContexts(securityContexts());

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("模型算法平台")
            .description("模型算法平台接口")
            .contact(new Contact("王威", null, "wei_wang_2019@163.com"))
            .version("1.0")
            .build();
    }

    private List<SecurityScheme> securitySchemes() {
        ApiKey apiKey = new ApiKey("access-token", "access-token", In.HEADER.toValue());
        return Collections.singletonList(apiKey);
    }


    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
            SecurityContext.builder()
                           .securityReferences(Collections.singletonList(
                               new SecurityReference("access-token", new AuthorizationScope[]{new AuthorizationScope("global", "")}))
                           )
                           .build()
        );
    }

}
