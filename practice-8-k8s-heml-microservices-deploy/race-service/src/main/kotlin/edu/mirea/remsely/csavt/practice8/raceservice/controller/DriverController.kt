package edu.mirea.remsely.csavt.practice8.raceservice.controller

import edu.mirea.remsely.csavt.practice8.raceservice.dto.DriverRequest
import edu.mirea.remsely.csavt.practice8.raceservice.dto.DriverResponse
import edu.mirea.remsely.csavt.practice8.raceservice.service.DriverService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/drivers")
class DriverController(
    private val driverService: DriverService
) {
    @GetMapping
    fun getAllDrivers(): ResponseEntity<List<DriverResponse>> {
        return ResponseEntity.ok(driverService.getAllDrivers())
    }

    @GetMapping("/{id}")
    fun getDriverById(@PathVariable id: Long): ResponseEntity<DriverResponse> {
        return ResponseEntity.ok(driverService.getDriverById(id))
    }

    @GetMapping("/team/{team}")
    fun getDriversByTeam(@PathVariable team: String): ResponseEntity<List<DriverResponse>> {
        return ResponseEntity.ok(driverService.getDriversByTeam(team))
    }

    @PostMapping
    fun createDriver(@Valid @RequestBody request: DriverRequest): ResponseEntity<DriverResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.createDriver(request))
    }

    @DeleteMapping("/{id}")
    fun deleteDriver(@PathVariable id: Long): ResponseEntity<Void> {
        driverService.deleteDriver(id)
        return ResponseEntity.noContent().build()
    }
}
