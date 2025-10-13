package edu.mirea.remsely.csavt.practice4.university.dto.response

data class StudentResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val studentNumber: String,
    val course: CourseBasicInfo? = null
)
