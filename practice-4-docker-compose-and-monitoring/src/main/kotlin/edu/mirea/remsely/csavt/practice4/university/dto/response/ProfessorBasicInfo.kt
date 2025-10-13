package edu.mirea.remsely.csavt.practice4.university.dto.response

data class ProfessorBasicInfo(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val department: String,
    val academicTitle: String?
)
