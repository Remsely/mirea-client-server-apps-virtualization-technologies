package edu.mirea.remsely.csavt.practice8.telemetryservice.service

import edu.mirea.remsely.csavt.practice8.telemetryservice.dto.TelemetryEventResponse
import edu.mirea.remsely.csavt.practice8.telemetryservice.dto.TelemetryStatsResponse
import edu.mirea.remsely.csavt.practice8.telemetryservice.entity.TelemetryEvent
import edu.mirea.remsely.csavt.practice8.telemetryservice.kafka.DriverEvent
import edu.mirea.remsely.csavt.practice8.telemetryservice.kafka.RaceEvent
import edu.mirea.remsely.csavt.practice8.telemetryservice.repository.TelemetryEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TelemetryService(
    private val telemetryEventRepository: TelemetryEventRepository
) {
    private val log = LoggerFactory.getLogger(TelemetryService::class.java)

    companion object {
        const val SOURCE_RACE = "race-service"
        const val SOURCE_DRIVER = "driver-service"
    }

    @Transactional
    fun processRaceEvent(event: RaceEvent, rawPayload: String) {
        log.info("Processing race event: {} for race: {}", event.eventType, event.raceName)

        val telemetryEvent = TelemetryEvent(
            eventType = event.eventType,
            source = SOURCE_RACE,
            entityId = event.raceId,
            entityName = event.raceName,
            payload = rawPayload
        )
        telemetryEventRepository.save(telemetryEvent)
    }

    @Transactional
    fun processDriverEvent(event: DriverEvent, rawPayload: String) {
        log.info("Processing driver event: {} for driver: {}", event.eventType, event.driverName)

        val telemetryEvent = TelemetryEvent(
            eventType = event.eventType,
            source = SOURCE_DRIVER,
            entityId = event.driverId,
            entityName = event.driverName,
            payload = rawPayload
        )
        telemetryEventRepository.save(telemetryEvent)
    }

    fun getAllEvents(): List<TelemetryEventResponse> {
        return telemetryEventRepository.findAll().map { it.toResponse() }
    }

    fun getEventsBySource(source: String): List<TelemetryEventResponse> {
        return telemetryEventRepository.findBySource(source).map { it.toResponse() }
    }

    fun getEventsByType(eventType: String): List<TelemetryEventResponse> {
        return telemetryEventRepository.findByEventType(eventType).map { it.toResponse() }
    }

    fun getEventById(id: Long): TelemetryEventResponse {
        return telemetryEventRepository.findById(id)
            .orElseThrow { NoSuchElementException("Telemetry event not found with id: $id") }
            .toResponse()
    }

    fun getStats(): TelemetryStatsResponse {
        val allEvents = telemetryEventRepository.findAll()
        return TelemetryStatsResponse(
            totalEvents = allEvents.size.toLong(),
            raceEvents = allEvents.count { it.source == SOURCE_RACE }.toLong(),
            driverEvents = allEvents.count { it.source == SOURCE_DRIVER }.toLong()
        )
    }

    private fun TelemetryEvent.toResponse() = TelemetryEventResponse(
        id = this.id,
        eventType = this.eventType,
        source = this.source,
        entityId = this.entityId,
        entityName = this.entityName,
        payload = this.payload,
        receivedAt = this.receivedAt
    )
}
