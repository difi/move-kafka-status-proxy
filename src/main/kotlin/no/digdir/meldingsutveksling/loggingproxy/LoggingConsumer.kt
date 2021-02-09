package no.digdir.meldingsutveksling.loggingproxy

import no.digdir.meldingsutveksling.loggingproxy.domain.StatusMessage
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@ConditionalOnProperty(name = ["digdir.move.loggingproxy.consume-topic"], havingValue = "true")
@Component
class LoggingConsumer {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(topics = ["\${digdir.move.loggingproxy.log-topic}"])
    fun logListener(cr: ConsumerRecord<Any, StatusMessage>) {
        log.info("New message on log-topic: $cr")
    }

    @KafkaListener(topics = ["\${digdir.move.loggingproxy.status-topic}"])
    fun statusListener(cr: ConsumerRecord<Any, StatusMessage>) {
        log.info("New message on status-topic: $cr")
    }

    @KafkaListener(topics = ["status-count"], containerFactory = "countListenerContainerFactory")
    fun countListener(cr: ConsumerRecord<StatusKey, Long>) {
        log.info("New count: Status - ${cr.key()} - value ${cr.value()}")
    }

}

data class StatusKey(
    val orgnr: String = "",
    val status: String = "",
    val service_identifier: String = "",
    val window_start: Long = 0L,
    val window_end: Long = 0L
)
