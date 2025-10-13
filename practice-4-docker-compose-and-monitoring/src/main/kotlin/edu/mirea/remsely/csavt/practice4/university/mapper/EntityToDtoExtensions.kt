package edu.mirea.remsely.csavt.practice4.university.mapper

import edu.mirea.remsely.csavt.practice4.university.dto.response.CourseBasicInfo
import edu.mirea.remsely.csavt.practice4.university.dto.response.CourseResponse
import edu.mirea.remsely.csavt.practice4.university.dto.response.ProfessorBasicInfo
import edu.mirea.remsely.csavt.practice4.university.dto.response.ProfessorResponse
import edu.mirea.remsely.csavt.practice4.university.dto.response.StudentResponse
import edu.mirea.remsely.csavt.practice4.university.entity.Course
import edu.mirea.remsely.csavt.practice4.university.entity.Professor
import edu.mirea.remsely.csavt.practice4.university.entity.Student

fun Student.toDto(): StudentResponse =
    StudentResponse(
        id = this.id!!,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        studentNumber = this.studentNumber,
        course = this.course?.let {
            CourseBasicInfo(
                id = it.id!!,
                name = it.name,
                credits = it.credits
            )
        }
    )

fun Course.toDto(): CourseResponse =
    CourseResponse(
        id = this.id!!,
        name = this.name,
        description = this.description,
        credits = this.credits,
        studentsCount = this.students.size,
        professors = this.professors.map { prof ->
            ProfessorBasicInfo(
                id = prof.id!!,
                firstName = prof.firstName,
                lastName = prof.lastName,
                department = prof.department,
                academicTitle = prof.academicTitle
            )
        }
    )

fun Professor.toDto(): ProfessorResponse =
    ProfessorResponse(
        id = this.id!!,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        department = this.department,
        academicTitle = this.academicTitle,
        courses = this.courses.map { course ->
            CourseBasicInfo(
                id = course.id!!,
                name = course.name,
                credits = course.credits
            )
        }
    )

@JvmName("toStudentDtos")
fun List<Student>.toDtos(): List<StudentResponse> = this.map { it.toDto() }

@JvmName("toCourseDtos")
fun List<Course>.toDtos(): List<CourseResponse> = this.map { it.toDto() }

@JvmName("toProfessorDtos")
fun List<Professor>.toDtos(): List<ProfessorResponse> = this.map { it.toDto() }
