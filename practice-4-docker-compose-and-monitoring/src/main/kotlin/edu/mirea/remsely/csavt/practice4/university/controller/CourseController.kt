package edu.mirea.remsely.csavt.practice4.university.controller

import edu.mirea.remsely.csavt.practice4.university.dto.request.CreateCourseRequest
import edu.mirea.remsely.csavt.practice4.university.dto.request.UpdateCourseRequest
import edu.mirea.remsely.csavt.practice4.university.dto.response.CourseResponse
import edu.mirea.remsely.csavt.practice4.university.service.CourseService
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/courses")
class CourseController(
    private val courseService: CourseService
) {
    @PostMapping
    fun createCourse(@Valid @RequestBody request: CreateCourseRequest): ResponseEntity<CourseResponse> {
        val course = courseService.createCourse(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(course)
    }

    @GetMapping
    fun getAllCourses(): ResponseEntity<List<CourseResponse>> {
        val courses = courseService.getAllCourses()
        return ResponseEntity.ok(courses)
    }

    @GetMapping("/{id}")
    fun getCourseById(@PathVariable id: Long): ResponseEntity<CourseResponse> {
        val course = courseService.getCourseById(id)
        return ResponseEntity.ok(course)
    }

    @PutMapping("/{id}")
    fun updateCourse(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateCourseRequest
    ): ResponseEntity<CourseResponse> {
        val course = courseService.updateCourse(id, request)
        return ResponseEntity.ok(course)
    }

    @DeleteMapping("/{id}")
    fun deleteCourse(@PathVariable id: Long): ResponseEntity<Void> {
        courseService.deleteCourse(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/search")
    fun searchCourses(@RequestParam name: String): ResponseEntity<List<CourseResponse>> {
        val courses = courseService.searchCourses(name)
        return ResponseEntity.ok(courses)
    }
}
