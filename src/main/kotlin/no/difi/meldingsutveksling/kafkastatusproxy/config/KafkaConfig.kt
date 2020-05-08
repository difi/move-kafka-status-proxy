package no.difi.meldingsutveksling.kafkastatusproxy.config

import no.difi.meldingsutveksling.kafkastatusproxy.domain.StatusMessage
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaConfig {

    @Bean
    fun producerFactory(kafkaProperties: KafkaProperties): ProducerFactory<String, StatusMessage> {
        return DefaultKafkaProducerFactory(kafkaProperties.buildProducerProperties())
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, StatusMessage>, props: StatusProxyProperties): KafkaTemplate<String, StatusMessage> {
        val kt = KafkaTemplate(producerFactory)
        kt.defaultTopic = props.topic
        return kt
    }

}