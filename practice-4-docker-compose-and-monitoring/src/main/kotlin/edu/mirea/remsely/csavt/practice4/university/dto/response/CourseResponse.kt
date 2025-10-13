package edu.mirea.remsely.csavt.practice4.university.dto.response

data class CourseResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val credits: Int,
    val studentsCount: Int,
    val professors: List<ProfessorBasicInfo> = emptyList()
)
