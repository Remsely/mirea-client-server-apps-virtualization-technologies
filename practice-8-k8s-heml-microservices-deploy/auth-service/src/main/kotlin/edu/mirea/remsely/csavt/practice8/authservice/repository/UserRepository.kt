package edu.mirea.remsely.csavt.practice8.authservice.repository

import edu.mirea.remsely.csavt.practice8.authservice.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}
