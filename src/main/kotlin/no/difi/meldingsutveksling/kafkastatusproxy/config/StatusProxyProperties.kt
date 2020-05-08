package no.difi.meldingsutveksling.kafkastatusproxy.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "digdir.move.statusproxy")
data class StatusProxyProperties(
        var topic: String = "",
        var testconsume: Boolean = true,
        var enableAuth: Boolean = false
)