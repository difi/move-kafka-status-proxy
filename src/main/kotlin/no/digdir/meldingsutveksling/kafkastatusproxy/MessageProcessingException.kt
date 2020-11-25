package no.digdir.meldingsutveksling.kafkastatusproxy

class MessageProcessingException : Exception {
    constructor(m: String): super(m)
    constructor(m: String, t: Throwable): super(m, t)
}