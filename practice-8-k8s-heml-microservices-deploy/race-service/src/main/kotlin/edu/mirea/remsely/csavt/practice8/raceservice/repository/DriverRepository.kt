package edu.mirea.remsely.csavt.practice8.raceservice.repository

import edu.mirea.remsely.csavt.practice8.raceservice.entity.Driver
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DriverRepository : JpaRepository<Driver, Long> {
    fun findByTeam(team: String): List<Driver>
}
