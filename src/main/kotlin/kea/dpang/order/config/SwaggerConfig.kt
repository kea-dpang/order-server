package kea.dpang.order.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import lombok.RequiredArgsConstructor
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

/**
 * Swagger(Spring Docs) 설정
 */
@OpenAPIDefinition(info = Info(title = "DPANG 주문 서비스 API 명세서", description = "DPANG 주문 서비스 API 명세서", version = "0.1.0"))
@RequiredArgsConstructor
@Configuration
class SwaggerConfig {

    fun securityBudiler(): OpenApiCustomizer {
        return OpenApiCustomizer { openApi: OpenAPI ->
            openApi.addSecurityItem(SecurityRequirement().addList("Access Token"))
                .components
                .addSecuritySchemes(
                    "Access Token", SecurityScheme()
                        .name(HttpHeaders.AUTHORIZATION)
                        .type(SecurityScheme.Type.HTTP)
                        .`in`(SecurityScheme.In.HEADER)
                        .bearerFormat("JWT")
                        .scheme("bearer")
                )
        }
    }

    @Bean
    fun openApi(): GroupedOpenApi {
        val paths = arrayOf("/**")
        return GroupedOpenApi.builder()
            .group("DPANG 주문 서비스 API")
            .pathsToMatch(*paths)
            .addOpenApiCustomizer(securityBudiler())
            .build()
    }
}