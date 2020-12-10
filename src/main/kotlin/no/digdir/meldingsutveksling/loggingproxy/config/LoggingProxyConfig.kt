package no.digdir.meldingsutveksling.loggingproxy.config

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

@Configuration
class LoggingProxyConfig {

    @Bean
    fun producerFactory(kafkaProperties: KafkaProperties): ProducerFactory<String, JsonNode> {
        return DefaultKafkaProducerFactory(kafkaProperties.buildProducerProperties())
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, JsonNode>, props: LoggingProxyProperties): KafkaTemplate<String, JsonNode> {
        val kt = KafkaTemplate(producerFactory)
        kt.defaultTopic = props.statusTopic
        return kt
    }

    @Bean
    fun kafkaSender(kafkaProperties: KafkaProperties): KafkaSender<Nothing, JsonNode> {
        val senderOptions = SenderOptions.create<Nothing, JsonNode>(kafkaProperties.buildProducerProperties())
        return KafkaSender.create(senderOptions)
    }

}