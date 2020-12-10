package no.digdir.meldingsutveksling.loggingproxy.config

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor

@EnableRSocketSecurity
@Configuration
class RSocketSecurityConfig {

    @Bean
    fun reactiveJwtDecoer(props: OAuth2ResourceServerProperties): ReactiveJwtDecoder {
        return NimbusReactiveJwtDecoder.withJwkSetUri(props.jwt.jwkSetUri).build()
    }

    @Bean
    fun rsocketInterceptor(rsocket: RSocketSecurity, props: LoggingProxyProperties): PayloadSocketAcceptorInterceptor {
        return if (props.enableAuth) {
            rsocket.authorizePayload {
                it.anyRequest().authenticated()
                        .anyExchange().permitAll()
            }
                    .jwt(Customizer.withDefaults())
        } else {
            rsocket.authorizePayload {
                it.anyRequest().permitAll()
                        .anyExchange().permitAll()
            }
        }.build()
    }

}
