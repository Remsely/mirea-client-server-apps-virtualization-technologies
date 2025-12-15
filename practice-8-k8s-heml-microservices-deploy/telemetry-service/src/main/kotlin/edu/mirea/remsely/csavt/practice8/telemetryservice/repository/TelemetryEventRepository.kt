package edu.mirea.remsely.csavt.practice8.telemetryservice.repository

import edu.mirea.remsely.csavt.practice8.telemetryservice.entity.TelemetryEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TelemetryEventRepository : JpaRepository<TelemetryEvent, Long> {
    fun findBySource(source: String): List<TelemetryEvent>
    fun findByEventType(eventType: String): List<TelemetryEvent>
    fun findBySourceAndEntityId(source: String, entityId: Long): List<TelemetryEvent>
}
