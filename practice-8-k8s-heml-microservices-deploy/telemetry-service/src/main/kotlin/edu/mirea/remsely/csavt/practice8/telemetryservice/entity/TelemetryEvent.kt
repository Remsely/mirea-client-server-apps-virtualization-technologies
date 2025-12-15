package edu.mirea.remsely.csavt.practice8.telemetryservice.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "telemetry_events")
class TelemetryEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val eventType: String,

    @Column(nullable = false)
    val source: String,

    @Column(nullable = false)
    val entityId: Long,

    @Column(nullable = false)
    val entityName: String,

    @Column(columnDefinition = "TEXT")
    val payload: String,

    @Column(nullable = false)
    val receivedAt: LocalDateTime = LocalDateTime.now()
)
