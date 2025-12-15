package edu.mirea.remsely.csavt.practice8.authservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 4, max = 100, message = "Password must be between 4 and 100 characters")
    val password: String
)

data class LoginRequest(
    @field:NotBlank(message = "Username is required")
    val username: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)

data class AuthResponse(
    val token: String,
    val username: String
)

data class TokenValidationResponse(
    val valid: Boolean,
    val username: String? = null
)

data class ErrorResponse(
    val error: String,
    val message: String
)

