package no.difi.meldingsutveksling.kafkastatusproxy

import no.difi.meldingsutveksling.kafkastatusproxy.domain.StatusMessage
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

        val iso6523Regex = "^([0-9]{4}:)([0-9]{9})(?::)?([0-9]{9})?$".toRegex()
        val personnrRegex = "^[0-9]{11}$".toRegex()
        val idRegex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}".toRegex()

        log.debug("Incoming request: $statusMessage")

        if (!iso6523Regex.matches(statusMessage.sender)
                || (!iso6523Regex.matches(statusMessage.receiver) && !personnrRegex.matches(statusMessage.receiver))) {
            return ResponseEntity.badRequest().body("Sender field must be valid ISO6523 format. Receiver must be either ISO6523 or personal identification number.")
        }

        if (!idRegex.matches(statusMessage.message_id) || !idRegex.matches(statusMessage.conversation_id)) {
            return ResponseEntity.badRequest().body("message_id and conversation_id must be of type UUID")
        }

        val groups = iso6523Regex.find(statusMessage.sender)?.groupValues?.filter { !it.isBlank() } ?: emptyList()
        when (groups.size) {
            3 -> statusMessage.sender_org_number = groups[2]
            4 -> statusMessage.sender_org_number = groups[3]
            else -> return ResponseEntity.badRequest().body("Range for ISO6523 not supported")
        }

        if (iso6523Regex.matches(statusMessage.receiver)) {
            statusMessage.receiver_org_number = iso6523Regex.find(statusMessage.receiver)?.groupValues?.get(2)
                    ?: statusMessage.receiver
        } else {
            statusMessage.receiver_org_number = statusMessage.receiver
        }

        kt.sendDefault(statusMessage)
        return ResponseEntity.ok().build()
    }

}