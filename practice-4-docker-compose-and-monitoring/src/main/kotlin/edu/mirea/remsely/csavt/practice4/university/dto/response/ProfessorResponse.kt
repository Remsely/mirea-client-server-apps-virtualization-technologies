package edu.mirea.remsely.csavt.practice4.university.dto.response

data class ProfessorResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val department: String,
    val academicTitle: String?,
    val courses: List<CourseBasicInfo> = emptyList()
)
