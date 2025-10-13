package edu.mirea.remsely.csavt.practice4.university.controller

import edu.mirea.remsely.csavt.practice4.university.dto.request.CreateProfessorRequest
import edu.mirea.remsely.csavt.practice4.university.dto.request.UpdateProfessorRequest
import edu.mirea.remsely.csavt.practice4.university.dto.response.ProfessorResponse
import edu.mirea.remsely.csavt.practice4.university.service.ProfessorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/professors")
class ProfessorController(
    private val professorService: ProfessorService
) {
    @PostMapping
    fun createProfessor(@Valid @RequestBody request: CreateProfessorRequest): ResponseEntity<ProfessorResponse> {
        val professor = professorService.createProfessor(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(professor)
    }

    @GetMapping
    fun getAllProfessors(): ResponseEntity<List<ProfessorResponse>> {
        val professors = professorService.getAllProfessors()
        return ResponseEntity.ok(professors)
    }

    @GetMapping("/{id}")
    fun getProfessorById(@PathVariable id: Long): ResponseEntity<ProfessorResponse> {
        val professor = professorService.getProfessorById(id)
        return ResponseEntity.ok(professor)
    }

    @PutMapping("/{id}")
    fun updateProfessor(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateProfessorRequest
    ): ResponseEntity<ProfessorResponse> {
        val professor = professorService.updateProfessor(id, request)
        return ResponseEntity.ok(professor)
    }

    @DeleteMapping("/{id}")
    fun deleteProfessor(@PathVariable id: Long): ResponseEntity<Void> {
        professorService.deleteProfessor(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/department/{department}")
    fun getProfessorsByDepartment(@PathVariable department: String): ResponseEntity<List<ProfessorResponse>> {
        val professors = professorService.getProfessorsByDepartment(department)
        return ResponseEntity.ok(professors)
    }
}
