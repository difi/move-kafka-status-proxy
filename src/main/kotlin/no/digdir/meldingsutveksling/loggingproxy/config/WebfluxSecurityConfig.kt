package no.digdir.meldingsutveksling.loggingproxy.config

import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.anyExchange
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebfluxSecurityConfig {

    @Bean
    fun userDetailsService(props: SecurityProperties): ReactiveUserDetailsService {
        val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
        val user = User.builder()
            .username(props.user.name)
            .password(props.user.password)
            .authorities("ADMIN")
            .passwordEncoder(encoder::encode)
            .build()
        return MapReactiveUserDetailsService(user)
    }

    @Bean
    @Order(1)
    fun actuatorFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .securityMatcher(anyExchange())
            .cors().and().csrf().disable()
            .authorizeExchange {
                it.pathMatchers("/actuator/health").permitAll()
                    .anyExchange().authenticated()
            }
            .httpBasic().and()
            .build()
    }

    @Bean
    @Order(0)
    fun permitAllChain(http: ServerHttpSecurity, props: LoggingProxyProperties): SecurityWebFilterChain {
        val builder = http
            .securityMatcher(pathMatchers("/api/**"))
            .cors().and().csrf().disable()
        return if (props.enableAuth) {
            builder.authorizeExchange {
                it.pathMatchers("/api/**").authenticated()
            }.oauth2ResourceServer {
                it.jwt(Customizer.withDefaults())
            }
        } else {
            builder.authorizeExchange {
                it.pathMatchers("/api/**").permitAll()
            }
        }.build()
    }

}