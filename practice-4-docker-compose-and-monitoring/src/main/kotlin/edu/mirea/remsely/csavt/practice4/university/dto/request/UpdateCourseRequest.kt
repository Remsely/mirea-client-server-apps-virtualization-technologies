package edu.mirea.remsely.csavt.practice4.university.dto.request

import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class UpdateCourseRequest(
    @field:Size(min = 3, max = 100)
    val name: String? = null,

    @field:Size(max = 500)
    val description: String? = null,

    @field:Positive
    val credits: Int? = null,

    val professorIds: List<Long>? = null
)
