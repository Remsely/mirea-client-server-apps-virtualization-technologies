package edu.mirea.remsely.csavt.practice8.raceservice.filter

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.crypto.SecretKey

@Component
class JwtAuthenticationFilter(
    @Value("\${jwt.secret}") private val secret: String
) : OncePerRequestFilter() {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Skip authentication for actuator endpoints
        if (request.requestURI.startsWith("/actuator")) {
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header")
            return
        }

        val token = authHeader.substring(7)

        try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload

            // Store username in request for controllers
            request.setAttribute("username", claims.subject)

            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            logger.warn("JWT validation failed: ${e.message}")
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token")
        }
    }
}

