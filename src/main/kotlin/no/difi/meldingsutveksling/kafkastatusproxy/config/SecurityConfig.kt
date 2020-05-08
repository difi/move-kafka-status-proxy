package no.difi.meldingsutveksling.kafkastatusproxy.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig {

    @Order(0)
    @Configuration
    class ActuatorConfig(val props: SecurityProperties) : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http.cors().and().csrf().disable()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            http.antMatcher("/actuator/**")
                    .authorizeRequests()
                    .antMatchers("/actuator/health").permitAll()
                    .antMatchers("/actuator/**").authenticated()
                    .and().httpBasic()
        }

        override fun configure(auth: AuthenticationManagerBuilder) {
            auth.inMemoryAuthentication().withUser(props.user.name)
                    .password("{noop}" + props.user.password).roles()
        }

    }

    @Order(1)
    @Configuration
    @ConditionalOnProperty(value = ["digdir.move.statusproxy.enable-auth"], havingValue = "true")
    class ApiSecurityConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http.cors().and().csrf().disable()
            http.antMatcher("/api/**")
                    .authorizeRequests()
                    .antMatchers("/api/**").authenticated()
                    .and().oauth2ResourceServer { o: OAuth2ResourceServerConfigurer<HttpSecurity> -> o.jwt() }
        }
    }

}
