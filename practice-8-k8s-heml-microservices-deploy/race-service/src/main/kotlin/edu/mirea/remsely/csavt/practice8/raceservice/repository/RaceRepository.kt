package edu.mirea.remsely.csavt.practice8.raceservice.repository

import edu.mirea.remsely.csavt.practice8.raceservice.entity.Race
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RaceRepository : JpaRepository<Race, Long> {
    fun findByCountry(country: String): List<Race>
}
