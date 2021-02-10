package no.digdir.meldingsutveksling.loggingproxy

import no.digdir.meldingsutveksling.loggingproxy.domain.StatusMessage

object MessageProcessor {

    fun process(statusMessage: StatusMessage) {

        val iso6523Regex = "^([0-9]{4}:)([0-9]{9})(?::)?([0-9]{9})?$".toRegex()
        val personnrRegex = "^[0-9]{11}$".toRegex()
        val idRegex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}".toRegex()

        if (!iso6523Regex.matches(statusMessage.sender)) {
            throw MessageProcessingException("Sender field (${statusMessage.sender}) is not valid ISO6523 format.")
        }

        // TODO fix for personal numbers
//        if (!iso6523Regex.matches(statusMessage.receiver) && !personnrRegex.matches(statusMessage.receiver)) {
//            throw MessageProcessingException("Receiver field (${statusMessage.receiver}) is not valid ISO6523 or personal identification number.")
//        }

        if (!idRegex.matches(statusMessage.message_id) || !idRegex.matches(statusMessage.conversation_id)) {
            throw MessageProcessingException("message_id and conversation_id must be of type UUID")
        }

        val groups = iso6523Regex.find(statusMessage.sender)?.groupValues?.filter { it.isNotBlank() } ?: emptyList()
        when (groups.size) {
            3 -> statusMessage.sender_org_number = groups[2]
            4 -> statusMessage.sender_org_number = groups[3]
            else -> throw MessageProcessingException("Range for ISO6523 not supported")
        }

        if (iso6523Regex.matches(statusMessage.receiver)) {
            statusMessage.receiver_org_number = iso6523Regex.find(statusMessage.receiver)?.groupValues?.get(2)
                    ?: statusMessage.receiver
        } else {
            statusMessage.receiver_org_number = statusMessage.receiver
        }
    }

}