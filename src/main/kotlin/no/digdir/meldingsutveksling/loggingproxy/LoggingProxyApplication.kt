package no.digdir.meldingsutveksling.loggingproxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LoggingProxyApplication

fun main(args: Array<String>) {
	runApplication<LoggingProxyApplication>(*args)
}
