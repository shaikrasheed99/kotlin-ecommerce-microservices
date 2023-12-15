package com.ecommerce.apigateway.configs

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayRoutesConfig {
    @Bean
    fun customRouteLocator(routeBuilder: RouteLocatorBuilder): RouteLocator {
        val extractPathPattern = "(?<remaining>.*)"
        val replacePathPattern = "/\${remaining}"
        return routeBuilder.routes()
            .route("order-service-route") { r ->
                r.path("/api/order/**")
                    .filters { f ->
                        f.rewritePath("/api/order/$extractPathPattern", replacePathPattern)
                    }
                    .uri("lb://order-service")
            }.build()
    }
}