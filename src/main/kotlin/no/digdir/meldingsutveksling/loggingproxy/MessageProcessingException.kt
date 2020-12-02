package no.digdir.meldingsutveksling.loggingproxy

class MessageProcessingException : Exception {
    constructor(m: String): super(m)
    constructor(m: String, t: Throwable): super(m, t)
}