package no.digdir.meldingsutveksling.kafkastatusproxy.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "digdir.move.statusproxy")
class StatusProxyProperties {
        lateinit var topic: String
        var consumeTopic: Boolean = false
        var enableAuth: Boolean = false
}
