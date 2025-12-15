package edu.mirea.remsely.csavt.practice8.raceservice.service

import edu.mirea.remsely.csavt.practice8.raceservice.dto.DriverRequest
import edu.mirea.remsely.csavt.practice8.raceservice.dto.DriverResponse
import edu.mirea.remsely.csavt.practice8.raceservice.entity.Driver
import edu.mirea.remsely.csavt.practice8.raceservice.kafka.DriverEvent
import edu.mirea.remsely.csavt.practice8.raceservice.kafka.KafkaProducerService
import edu.mirea.remsely.csavt.practice8.raceservice.repository.DriverRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DriverService(
    private val driverRepository: DriverRepository,
    private val kafkaProducerService: KafkaProducerService
) {
    fun getAllDrivers(): List<DriverResponse> {
        return driverRepository.findAll().map { it.toResponse() }
    }

    fun getDriverById(id: Long): DriverResponse {
        return driverRepository.findById(id)
            .orElseThrow { NoSuchElementException("Driver not found with id: $id") }
            .toResponse()
    }

    fun getDriversByTeam(team: String): List<DriverResponse> {
        return driverRepository.findByTeam(team).map { it.toResponse() }
    }

    @Transactional
    fun createDriver(request: DriverRequest): DriverResponse {
        val driver = Driver(
            name = request.name,
            team = request.team,
            number = request.number
        )
        val saved = driverRepository.save(driver)

        // Send event to Kafka
        kafkaProducerService.sendDriverEvent(
            DriverEvent(
                eventType = "DRIVER_CREATED",
                driverId = saved.id,
                driverName = saved.name,
                team = saved.team,
                number = saved.number
            )
        )

        return saved.toResponse()
    }

    @Transactional
    fun deleteDriver(id: Long) {
        val driver = driverRepository.findById(id)
            .orElseThrow { NoSuchElementException("Driver not found with id: $id") }

        driverRepository.delete(driver)

        // Send event to Kafka
        kafkaProducerService.sendDriverEvent(
            DriverEvent(
                eventType = "DRIVER_DELETED",
                driverId = driver.id,
                driverName = driver.name,
                team = driver.team,
                number = driver.number
            )
        )
    }

    private fun Driver.toResponse() = DriverResponse(
        id = this.id,
        name = this.name,
        team = this.team,
        number = this.number
    )
}
