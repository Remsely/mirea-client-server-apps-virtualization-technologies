package edu.mirea.remsely.csavt.practice4.university.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateStudentRequest(
    @field:NotBlank(message = "Имя не может быть пустым")
    @field:Size(min = 2, max = 50)
    val firstName: String,

    @field:NotBlank(message = "Фамилия не может быть пустой")
    @field:Size(min = 2, max = 50)
    val lastName: String,

    @field:NotBlank(message = "Email не может быть пустым")
    @field:Email(message = "Некорректный формат email")
    val email: String,

    @field:NotBlank(message = "Номер студенческого билета не может быть пустым")
    val studentNumber: String,

    val courseId: Long? = null
)
