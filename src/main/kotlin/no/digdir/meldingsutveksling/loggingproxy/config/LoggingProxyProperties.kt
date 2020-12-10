package no.digdir.meldingsutveksling.loggingproxy.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "digdir.move.loggingproxy")
class LoggingProxyProperties {
    lateinit var logTopic: String
    lateinit var statusTopic: String
    var consumeTopic: Boolean = false
    var enableAuth: Boolean = false
}
