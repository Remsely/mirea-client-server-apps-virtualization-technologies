package edu.mirea.remsely.csavt.practice4.university.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class CreateCourseRequest(
    @field:NotBlank(message = "Название курса не может быть пустым")
    @field:Size(min = 3, max = 100)
    val name: String,

    @field:Size(max = 500)
    val description: String? = null,

    @field:Positive(message = "Количество кредитов должно быть положительным")
    val credits: Int,

    val professorIds: List<Long> = emptyList()
)
