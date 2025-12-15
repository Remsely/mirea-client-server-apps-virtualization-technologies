package edu.mirea.remsely.csavt.practice8.authservice.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey

@Service
class JwtService(
    private val redisTemplate: StringRedisTemplate,
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration-ms}") private val expirationMs: Long
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(username: String): String {
        val now = Date()
        val expiry = Date(now.time + expirationMs)

        val token = Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()

        // Store token in Redis with expiration
        val redisKey = "token:$username"
        redisTemplate.opsForValue().set(redisKey, token, expirationMs, TimeUnit.MILLISECONDS)

        return token
    }

    fun validateToken(token: String): String? {
        return try {
            val claims = parseToken(token)
            val username = claims.subject

            // Check if token exists in Redis
            val redisKey = "token:$username"
            val storedToken = redisTemplate.opsForValue().get(redisKey)

            if (storedToken == token) {
                username
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun invalidateToken(username: String) {
        val redisKey = "token:$username"
        redisTemplate.delete(redisKey)
    }

    private fun parseToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
