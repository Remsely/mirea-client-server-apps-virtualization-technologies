package edu.mirea.remsely.csavt.practice4.university.repository

import edu.mirea.remsely.csavt.practice4.university.entity.Course
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CourseRepository : JpaRepository<Course, Long> {
    fun findByName(name: String): Course?

    @Query("SELECT c FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    fun searchByName(name: String): List<Course>
}
