package com.poppin.poppinserver.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT") // JWT 토큰 포맷 지정
                                .name("Authorization"))) // 헤더 이름 지정
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth")) // Security 설정 추가
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
