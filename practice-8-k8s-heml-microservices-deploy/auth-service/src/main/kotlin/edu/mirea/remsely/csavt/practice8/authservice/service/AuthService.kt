package edu.mirea.remsely.csavt.practice8.authservice.service

import edu.mirea.remsely.csavt.practice8.authservice.dto.AuthResponse
import edu.mirea.remsely.csavt.practice8.authservice.dto.LoginRequest
import edu.mirea.remsely.csavt.practice8.authservice.dto.RegisterRequest
import edu.mirea.remsely.csavt.practice8.authservice.dto.TokenValidationResponse
import edu.mirea.remsely.csavt.practice8.authservice.entity.User
import edu.mirea.remsely.csavt.practice8.authservice.exception.AuthException
import edu.mirea.remsely.csavt.practice8.authservice.repository.UserRepository
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw AuthException("User with username '${request.username}' already exists")
        }

        val hashedPassword = hashPassword(request.password)
        val user = User(
            username = request.username,
            password = hashedPassword
        )

        userRepository.save(user)
        val token = jwtService.generateToken(user.username)

        return AuthResponse(token = token, username = user.username)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByUsername(request.username)
            ?: throw AuthException("Invalid username or password")

        val hashedPassword = hashPassword(request.password)
        if (user.password != hashedPassword) {
            throw AuthException("Invalid username or password")
        }

        // Invalidate old token and generate new one
        jwtService.invalidateToken(user.username)
        val token = jwtService.generateToken(user.username)

        return AuthResponse(token = token, username = user.username)
    }

    fun validateToken(token: String): TokenValidationResponse {
        val username = jwtService.validateToken(token)
        return TokenValidationResponse(
            valid = username != null,
            username = username
        )
    }

    fun logout(token: String) {
        val username = jwtService.validateToken(token)
        if (username != null) {
            jwtService.invalidateToken(username)
        }
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }
}
