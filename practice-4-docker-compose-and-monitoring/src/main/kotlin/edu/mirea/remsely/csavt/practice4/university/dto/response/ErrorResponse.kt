package edu.mirea.remsely.csavt.practice4.university.dto.response

data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
    val errors: Map<String, String>? = null,
    val timestamp: Long = System.currentTimeMillis()
)
