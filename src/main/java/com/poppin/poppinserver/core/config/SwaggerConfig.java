package com.poppin.poppinserver.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Poppin Springdoc")
                .description("Poppin Swagger Documentation")
                .version("1.0.0");
    }

    // 스웨거 커스터마이징
    @Bean
    public GroupedOpenApi taggedApis() {
        return GroupedOpenApi.builder()
                .group("Tagged APIs")
                .pathsToMatch("/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    // Operation에 @Operation 어노테이션이 없으면 제외
                    if (handlerMethod.getMethodAnnotation(io.swagger.v3.oas.annotations.Operation.class) == null) {
                        return null; // 제외
                    }
                    return operation;
                })
                .build();
    }
}