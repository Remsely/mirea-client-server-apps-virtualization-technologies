package edu.mirea.remsely.csavt.practice4.university.repository

import edu.mirea.remsely.csavt.practice4.university.entity.Professor
import org.springframework.data.jpa.repository.JpaRepository

interface ProfessorRepository : JpaRepository<Professor, Long> {
    fun findByEmail(email: String): Professor?

    fun findByDepartment(department: String): List<Professor>
}
