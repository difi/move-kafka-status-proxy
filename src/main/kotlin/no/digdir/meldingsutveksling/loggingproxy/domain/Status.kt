package no.digdir.meldingsutveksling.loggingproxy.domain

enum class Status {
    OPPRETTET,
    SENDT,
    MOTTATT,
    LEVERT,
    LEST,
    FEIL,
    ANNET,
    INNKOMMENDE_MOTTATT,
    INNKOMMENDE_LEVERT,
    LEVETID_UTLOPT
}