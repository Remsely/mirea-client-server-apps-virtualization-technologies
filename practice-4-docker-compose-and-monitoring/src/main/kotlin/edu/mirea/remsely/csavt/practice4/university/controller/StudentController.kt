package edu.mirea.remsely.csavt.practice4.university.controller

import edu.mirea.remsely.csavt.practice4.university.dto.request.CreateStudentRequest
import edu.mirea.remsely.csavt.practice4.university.dto.request.UpdateStudentRequest
import edu.mirea.remsely.csavt.practice4.university.dto.response.StudentResponse
import edu.mirea.remsely.csavt.practice4.university.service.StudentService
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
@RequestMapping("/api/students")
class StudentController(
    private val studentService: StudentService
) {
    @PostMapping
    fun createStudent(@Valid @RequestBody request: CreateStudentRequest): ResponseEntity<StudentResponse> {
        val student = studentService.createStudent(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(student)
    }

    @GetMapping
    fun getAllStudents(): ResponseEntity<List<StudentResponse>> {
        val students = studentService.getAllStudents()
        return ResponseEntity.ok(students)
    }

    @GetMapping("/{id}")
    fun getStudentById(@PathVariable id: Long): ResponseEntity<StudentResponse> {
        val student = studentService.getStudentById(id)
        return ResponseEntity.ok(student)
    }

    @PutMapping("/{id}")
    fun updateStudent(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateStudentRequest
    ): ResponseEntity<StudentResponse> {
        val student = studentService.updateStudent(id, request)
        return ResponseEntity.ok(student)
    }

    @DeleteMapping("/{id}")
    fun deleteStudent(@PathVariable id: Long): ResponseEntity<Void> {
        studentService.deleteStudent(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/course/{courseId}")
    fun getStudentsByCourse(@PathVariable courseId: Long): ResponseEntity<List<StudentResponse>> {
        val students = studentService.getStudentsByCourse(courseId)
        return ResponseEntity.ok(students)
    }
}
