package edu.mirea.remsely.csavt.practice8.raceservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDate

// Driver DTOs
data class DriverRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Team is required")
    val team: String,

    @field:Positive(message = "Number must be positive")
    val number: Int
)

data class DriverResponse(
    val id: Long,
    val name: String,
    val team: String,
    val number: Int
)

// Race DTOs
data class RaceRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Circuit is required")
    val circuit: String,

    val date: LocalDate,

    @field:NotBlank(message = "Country is required")
    val country: String
)

data class RaceResponse(
    val id: Long,
    val name: String,
    val circuit: String,
    val date: LocalDate,
    val country: String
)

data class ErrorResponse(
    val error: String,
    val message: String
)
