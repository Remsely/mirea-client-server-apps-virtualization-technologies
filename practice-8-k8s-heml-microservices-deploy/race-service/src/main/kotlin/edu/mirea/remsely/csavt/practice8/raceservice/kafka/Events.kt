package edu.mirea.remsely.csavt.practice8.raceservice.kafka

import java.time.LocalDate

data class RaceEvent(
    val eventType: String,
    val raceId: Long,
    val raceName: String,
    val circuit: String,
    val date: LocalDate,
    val country: String
)

data class DriverEvent(
    val eventType: String,
    val driverId: Long,
    val driverName: String,
    val team: String,
    val number: Int
)
