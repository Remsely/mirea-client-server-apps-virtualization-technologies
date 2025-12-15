package edu.mirea.remsely.csavt.practice8.telemetryservice.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import edu.mirea.remsely.csavt.practice8.telemetryservice.service.TelemetryService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaConsumerService(
    private val telemetryService: TelemetryService,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(KafkaConsumerService::class.java)

    @KafkaListener(topics = ["race-events"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeRaceEvents(message: String) {
        log.info("Received race event: {}", message)
        try {
            val event = objectMapper.readValue(message, RaceEvent::class.java)
            telemetryService.processRaceEvent(event, message)
        } catch (e: Exception) {
            log.error("Error processing race event: {}", e.message)
        }
    }

    @KafkaListener(topics = ["driver-events"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeDriverEvents(message: String) {
        log.info("Received driver event: {}", message)
        try {
            val event = objectMapper.readValue(message, DriverEvent::class.java)
            telemetryService.processDriverEvent(event, message)
        } catch (e: Exception) {
            log.error("Error processing driver event: {}", e.message)
        }
    }
}
