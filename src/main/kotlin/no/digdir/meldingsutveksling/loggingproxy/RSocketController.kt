package no.digdir.meldingsutveksling.loggingproxy

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import no.digdir.meldingsutveksling.loggingproxy.config.StatusProxyProperties
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
class RSocketController(val kafkaSender: KafkaSender<Nothing, JsonNode>,
                        val props: StatusProxyProperties) {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @MessageMapping("log-fire")
    fun posttest(s: Mono<String>) {
        val mono = s.map { SenderRecord.create(ProducerRecord<Nothing, JsonNode>(props.topic, TextNode.valueOf(it)), null) }
                .onErrorResume {
                    log.warn("Error processing request - dumping", it)
                    Mono.empty()
                }
                .doOnNext { log.debug("Sending $it") }
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