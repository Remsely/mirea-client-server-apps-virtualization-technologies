package edu.mirea.remsely.csavt.practice8.raceservice.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(KafkaProducerService::class.java)

    companion object {
        const val RACE_EVENTS_TOPIC = "race-events"
        const val DRIVER_EVENTS_TOPIC = "driver-events"
    }

    fun sendRaceEvent(event: RaceEvent) {
        val message = objectMapper.writeValueAsString(event)
        log.info("Sending race event to Kafka: {}", event)
        kafkaTemplate.send(RACE_EVENTS_TOPIC, event.raceId.toString(), message)
    }

    fun sendDriverEvent(event: DriverEvent) {
        val message = objectMapper.writeValueAsString(event)
        log.info("Sending driver event to Kafka: {}", event)
        kafkaTemplate.send(DRIVER_EVENTS_TOPIC, event.driverId.toString(), message)
    }
}
