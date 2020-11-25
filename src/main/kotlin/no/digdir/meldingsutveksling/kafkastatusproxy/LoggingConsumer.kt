package no.digdir.meldingsutveksling.kafkastatusproxy

import no.digdir.meldingsutveksling.kafkastatusproxy.domain.StatusMessage
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@ConditionalOnProperty(name = ["digdir.move.statusproxy.consume-topic"], havingValue = "true")
@Component
class LoggingConsumer {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(topics = ["\${digdir.move.statusproxy.topic}"])
    fun listener(cr: ConsumerRecord<Any, StatusMessage>) {
        log.info("New message on topic: $cr")
    }

}