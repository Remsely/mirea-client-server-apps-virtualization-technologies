package edu.mirea.remsely.csavt.practice8.raceservice.service

import edu.mirea.remsely.csavt.practice8.raceservice.dto.RaceRequest
import edu.mirea.remsely.csavt.practice8.raceservice.dto.RaceResponse
import edu.mirea.remsely.csavt.practice8.raceservice.entity.Race
import edu.mirea.remsely.csavt.practice8.raceservice.kafka.KafkaProducerService
import edu.mirea.remsely.csavt.practice8.raceservice.kafka.RaceEvent
import edu.mirea.remsely.csavt.practice8.raceservice.repository.RaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RaceService(
    private val raceRepository: RaceRepository,
    private val kafkaProducerService: KafkaProducerService
) {
    fun getAllRaces(): List<RaceResponse> {
        return raceRepository.findAll().map { it.toResponse() }
    }

    fun getRaceById(id: Long): RaceResponse {
        return raceRepository.findById(id)
            .orElseThrow { NoSuchElementException("Race not found with id: $id") }
            .toResponse()
    }

    fun getRacesByCountry(country: String): List<RaceResponse> {
        return raceRepository.findByCountry(country).map { it.toResponse() }
    }

    @Transactional
    fun createRace(request: RaceRequest): RaceResponse {
        val race = Race(
            name = request.name,
            circuit = request.circuit,
            date = request.date,
            country = request.country
        )
        val saved = raceRepository.save(race)

        // Send event to Kafka
        kafkaProducerService.sendRaceEvent(
            RaceEvent(
                eventType = "RACE_CREATED",
                raceId = saved.id,
                raceName = saved.name,
                circuit = saved.circuit,
                date = saved.date,
                country = saved.country
            )
        )

        return saved.toResponse()
    }

    @Transactional
    fun deleteRace(id: Long) {
        val race = raceRepository.findById(id)
            .orElseThrow { NoSuchElementException("Race not found with id: $id") }

        raceRepository.delete(race)

        // Send event to Kafka
        kafkaProducerService.sendRaceEvent(
            RaceEvent(
                eventType = "RACE_DELETED",
                raceId = race.id,
                raceName = race.name,
                circuit = race.circuit,
                date = race.date,
                country = race.country
            )
        )
    }

    private fun Race.toResponse() = RaceResponse(
        id = this.id,
        name = this.name,
        circuit = this.circuit,
        date = this.date,
        country = this.country
    )
}
