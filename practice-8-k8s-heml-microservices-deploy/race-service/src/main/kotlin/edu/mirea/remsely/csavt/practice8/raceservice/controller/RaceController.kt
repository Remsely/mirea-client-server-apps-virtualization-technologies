package edu.mirea.remsely.csavt.practice8.raceservice.controller

import edu.mirea.remsely.csavt.practice8.raceservice.dto.RaceRequest
import edu.mirea.remsely.csavt.practice8.raceservice.dto.RaceResponse
import edu.mirea.remsely.csavt.practice8.raceservice.service.RaceService
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
@RequestMapping("/api/races")
class RaceController(
    private val raceService: RaceService
) {
    @GetMapping
    fun getAllRaces(): ResponseEntity<List<RaceResponse>> {
        return ResponseEntity.ok(raceService.getAllRaces())
    }

    @GetMapping("/{id}")
    fun getRaceById(@PathVariable id: Long): ResponseEntity<RaceResponse> {
        return ResponseEntity.ok(raceService.getRaceById(id))
    }

    @GetMapping("/country/{country}")
    fun getRacesByCountry(@PathVariable country: String): ResponseEntity<List<RaceResponse>> {
        return ResponseEntity.ok(raceService.getRacesByCountry(country))
    }

    @PostMapping
    fun createRace(@Valid @RequestBody request: RaceRequest): ResponseEntity<RaceResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.createRace(request))
    }

    @DeleteMapping("/{id}")
    fun deleteRace(@PathVariable id: Long): ResponseEntity<Void> {
        raceService.deleteRace(id)
        return ResponseEntity.noContent().build()
    }
}
