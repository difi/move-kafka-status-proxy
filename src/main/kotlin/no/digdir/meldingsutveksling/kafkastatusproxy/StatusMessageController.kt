package no.digdir.meldingsutveksling.kafkastatusproxy

import no.digdir.meldingsutveksling.kafkastatusproxy.domain.StatusMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/status")
class StatusMessageController(val kt: KafkaTemplate<String, StatusMessage>) {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun postStatus(@RequestBody statusMessage: StatusMessage): ResponseEntity<Any> {
        log.trace("Incoming request: $statusMessage")

        try {
            MessageProcessor.process(statusMessage)
        } catch (e: MessageProcessingException) {
            return ResponseEntity.badRequest().body(e.message)
        }

        kt.sendDefault(statusMessage)
        return ResponseEntity.ok().build()
    }

}