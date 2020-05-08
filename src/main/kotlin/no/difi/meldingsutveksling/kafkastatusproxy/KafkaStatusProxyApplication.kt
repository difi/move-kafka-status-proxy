package no.difi.meldingsutveksling.kafkastatusproxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaStatusProxyApplication

fun main(args: Array<String>) {
	runApplication<KafkaStatusProxyApplication>(*args)
}
