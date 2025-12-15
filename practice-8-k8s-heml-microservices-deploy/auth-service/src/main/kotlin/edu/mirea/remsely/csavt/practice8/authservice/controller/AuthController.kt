package edu.mirea.remsely.csavt.practice8.authservice.controller

import edu.mirea.remsely.csavt.practice8.authservice.dto.AuthResponse
import edu.mirea.remsely.csavt.practice8.authservice.dto.LoginRequest
import edu.mirea.remsely.csavt.practice8.authservice.dto.RegisterRequest
import edu.mirea.remsely.csavt.practice8.authservice.dto.TokenValidationResponse
import edu.mirea.remsely.csavt.practice8.authservice.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = authService.register(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/validate")
    fun validateToken(@RequestHeader("Authorization") authHeader: String): ResponseEntity<TokenValidationResponse> {
        val token = authHeader.removePrefix("Bearer ").trim()
        val response = authService.validateToken(token)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Map<String, String>> {
        val token = authHeader.removePrefix("Bearer ").trim()
        authService.logout(token)
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }
}

