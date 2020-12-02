package no.digdir.meldingsutveksling.loggingproxy

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.digdir.meldingsutveksling.loggingproxy.domain.StatusMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class StatusMessageController(val kt: KafkaTemplate<String, JsonNode>) {

    val log: Logger = LoggerFactory.getLogger(this::class.java)
    val om = ObjectMapper()

    @PostMapping
    fun postLog(@RequestBody body: JsonNode): ResponseEntity<Any> {
        log.trace("Received: ${body.toPrettyString()}")
        kt.sendDefault(body)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/status")
    fun postStatus(@RequestBody statusMessage: StatusMessage): ResponseEntity<Any> {
        log.trace("Received status: $statusMessage")

        try {
            MessageProcessor.process(statusMessage)
        } catch (e: MessageProcessingException) {
            return ResponseEntity.badRequest().body(e.message)
        }

        kt.sendDefault(om.valueToTree(statusMessage))
        return ResponseEntity.ok().build()
    }

}