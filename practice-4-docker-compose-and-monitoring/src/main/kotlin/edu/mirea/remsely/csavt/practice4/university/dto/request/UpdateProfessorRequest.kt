package edu.mirea.remsely.csavt.practice4.university.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UpdateProfessorRequest(
    @field:Size(min = 2, max = 50)
    val firstName: String? = null,

    @field:Size(min = 2, max = 50)
    val lastName: String? = null,

    @field:Email
    val email: String? = null,

    val department: String? = null,

    val academicTitle: String? = null
)
