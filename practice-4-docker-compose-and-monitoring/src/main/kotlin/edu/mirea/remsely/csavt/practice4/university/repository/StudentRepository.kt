package edu.mirea.remsely.csavt.practice4.university.repository

import edu.mirea.remsely.csavt.practice4.university.entity.Student
import org.springframework.data.jpa.repository.JpaRepository

interface StudentRepository : JpaRepository<Student, Long> {
    fun findByEmail(email: String): Student?

    fun findByStudentNumber(studentNumber: String): Student?

    fun findByCourseId(courseId: Long): List<Student>
}
