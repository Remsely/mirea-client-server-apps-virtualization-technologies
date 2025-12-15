package edu.mirea.remsely.csavt.practice8.telemetryservice.controller

import edu.mirea.remsely.csavt.practice8.telemetryservice.dto.TelemetryEventResponse
import edu.mirea.remsely.csavt.practice8.telemetryservice.dto.TelemetryStatsResponse
import edu.mirea.remsely.csavt.practice8.telemetryservice.service.TelemetryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/telemetry")
class TelemetryController(
    private val telemetryService: TelemetryService
) {
    @GetMapping
    fun getAllEvents(): ResponseEntity<List<TelemetryEventResponse>> {
        return ResponseEntity.ok(telemetryService.getAllEvents())
    }

    @GetMapping("/{id}")
    fun getEventById(@PathVariable id: Long): ResponseEntity<TelemetryEventResponse> {
        return ResponseEntity.ok(telemetryService.getEventById(id))
    }

    @GetMapping("/source/{source}")
    fun getEventsBySource(@PathVariable source: String): ResponseEntity<List<TelemetryEventResponse>> {
        return ResponseEntity.ok(telemetryService.getEventsBySource(source))
    }

    @GetMapping("/type/{eventType}")
    fun getEventsByType(@PathVariable eventType: String): ResponseEntity<List<TelemetryEventResponse>> {
        return ResponseEntity.ok(telemetryService.getEventsByType(eventType))
    }

    @GetMapping("/stats")
    fun getStats(): ResponseEntity<TelemetryStatsResponse> {
        return ResponseEntity.ok(telemetryService.getStats())
    }
}
