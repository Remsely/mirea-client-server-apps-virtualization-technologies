package edu.mirea.remsely.csavt.practice8.authservice.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val method = request.method
        val uri = request.requestURI
        val clientIp = getClientIp(request)

        MDC.put("http_method", method)
        MDC.put("request_uri", uri)
        MDC.put("client_ip", clientIp)

        try {
            log.info("Incoming request: {} {} from {}", method, uri, clientIp)
            filterChain.doFilter(request, response)
            log.info("Completed request: {} {} - Status: {}", method, uri, response.status)
        } finally {
            MDC.clear()
        }
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        return if (!xForwardedFor.isNullOrBlank()) {
            xForwardedFor.split(",").first().trim()
        } else {
            request.remoteAddr
        }
    }
}
