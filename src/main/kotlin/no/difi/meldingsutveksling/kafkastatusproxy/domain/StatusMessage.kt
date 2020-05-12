package no.difi.meldingsutveksling.kafkastatusproxy.domain

data class StatusMessage(
        val status: Status,
        val conversation_id: String,
        val message_id: String,
        val orgnr: String,
        val process_identifier: String,
        val document_identifier: String,
        val sender: String,
        val receiver: String,
        var receiver_org_number: String = "",
        var sender_org_number: String = "",
        val service_identifier: String,
        val logger_name: String = "STATUS",
        val loglevel: String = "INFO"
)
