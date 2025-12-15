package edu.mirea.remsely.csavt.practice8.telemetryservice.dto

import java.time.LocalDateTime

data class TelemetryEventResponse(
    val id: Long,
    val eventType: String,
    val source: String,
    val entityId: Long,
    val entityName: String,
    val payload: String,
    val receivedAt: LocalDateTime
)

data class TelemetryStatsResponse(
    val totalEvents: Long,
    val raceEvents: Long,
    val driverEvents: Long
)

data class ErrorResponse(
    val error: String,
    val message: String
)
