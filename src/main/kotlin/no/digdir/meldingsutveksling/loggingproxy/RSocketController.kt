package no.digdir.meldingsutveksling.loggingproxy

import com.fasterxml.jackson.databind.JsonNode
import no.digdir.meldingsutveksling.loggingproxy.config.LoggingProxyProperties
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord

@Controller
class RSocketController(
    val kafkaSender: KafkaSender<Nothing, JsonNode>,
    val props: LoggingProxyProperties
) {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @MessageMapping("log-fire")
    fun postlog(s: Mono<JsonNode>) {
        val mono = s.map {
            when (it["logger_name"].textValue()) {
                "STATUS" -> SenderRecord.create(ProducerRecord<Nothing, JsonNode>(props.statusTopic, it), null)
                else -> SenderRecord.create(ProducerRecord<Nothing, JsonNode>(props.logTopic, it), null)
            }
        }.onErrorResume {
            log.warn("Error processing request - dumping", it)
            Mono.empty()
        }.doOnNext { log.debug("Sending $it") }
        kafkaSender.send(mono)
            .doOnError { log.error("Error sending record to kafka", it) }
            .doOnNext { log.debug("Record sent to kafka: ${it.recordMetadata()}") }
            .subscribe()
    }

    @MessageExceptionHandler
    fun handleException(e: Exception): Mono<Throwable> {
        log.warn("Exception while processing message: ${e.message}", e)
        return Mono.error(e)
    }

}