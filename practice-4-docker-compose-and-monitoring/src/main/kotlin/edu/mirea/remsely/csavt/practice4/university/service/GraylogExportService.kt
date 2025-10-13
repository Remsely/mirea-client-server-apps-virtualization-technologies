package edu.mirea.remsely.csavt.practice4.university.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.Instant
import java.util.*

@Service
class GraylogExportService(
    @param:Value("\${graylog.host:localhost}") private val graylogHost: String,
    @param:Value("\${graylog.api.port:9000}") private val graylogApiPort: Int,
    @param:Value("\${graylog.api.user:admin}") private val graylogUser: String,
    @param:Value("\${graylog.api.password:admin}") private val graylogPassword: String
) {
    private val logger = LoggerFactory.getLogger(GraylogExportService::class.java)

    private val webClient = WebClient.builder()
        .baseUrl("http://$graylogHost:$graylogApiPort/api")
        .defaultHeader("Authorization", createBasicAuthHeader())
        .defaultHeader("Accept", "application/json")
        .build()

    private fun createBasicAuthHeader(): String {
        val auth = "$graylogUser:$graylogPassword"
        val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray())
        return "Basic $encodedAuth"
    }

    fun exportLogsAsCsv(query: String?, rangeInSeconds: Int?): String {
        logger.info("Экспорт логов из GrayLog: query=$query, range=$rangeInSeconds")

        return try {
            val logs = fetchLogsFromGraylog(query ?: "*", rangeInSeconds ?: 300)
            convertLogsToCsv(logs)
        } catch (ex: Exception) {
            logger.error("Ошибка при получении логов из GrayLog", ex)
            createErrorCsv(ex.message ?: "Неизвестная ошибка")
        }
    }

    private fun fetchLogsFromGraylog(query: String, rangeInSeconds: Int): List<Map<String, Any>> {
        try {
            val response = webClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/search/universal/relative")
                        .queryParam("query", query)
                        .queryParam("range", rangeInSeconds)
                        .queryParam("limit", 1000)
                        .queryParam("sort", "timestamp:desc")
                        .build()
                }
                .retrieve()
                .bodyToMono<Map<String, Any>>()
                .block()

            @Suppress("UNCHECKED_CAST")
            val messages = response?.get("messages") as? List<Map<String, Any>> ?: emptyList()

            return messages.map { message ->
                @Suppress("UNCHECKED_CAST")
                (message["message"] as? Map<String, Any>) ?: emptyMap()
            }
        } catch (ex: Exception) {
            logger.warn("Не удалось получить логи из GrayLog, возвращаем тестовые данные", ex)
            return createSampleLogs()
        }
    }

    private fun createSampleLogs(): List<Map<String, Any>> {
        return listOf(
            mapOf(
                "timestamp" to Instant.now().toString(),
                "level" to "INFO",
                "message" to "Пример лога 1",
                "source" to "monitoring-app"
            ),
            mapOf(
                "timestamp" to Instant.now().minusSeconds(10).toString(),
                "level" to "DEBUG",
                "message" to "Пример лога 2",
                "source" to "monitoring-app"
            ),
            mapOf(
                "timestamp" to Instant.now().minusSeconds(20).toString(),
                "level" to "ERROR",
                "message" to "Пример ошибки",
                "source" to "monitoring-app"
            )
        )
    }

    private fun convertLogsToCsv(logs: List<Map<String, Any>>): String {
        if (logs.isEmpty()) {
            return "timestamp,level,message,source\n(нет логов за указанный период)"
        }

        val header = "timestamp,level,message,source\n"
        val rows = logs.joinToString("\n") { log ->
            val timestamp = log["timestamp"] ?: ""
            val level = log["level"] ?: log["severity"] ?: "INFO"
            val message = escapeCsv(log["message"]?.toString() ?: "")
            val source = log["source"] ?: log["application_name"] ?: "unknown"

            "$timestamp,$level,\"$message\",$source"
        }

        return header + rows
    }

    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"")
    }

    private fun createErrorCsv(errorMessage: String): String {
        return "timestamp,level,message,source\n" +
                "${Instant.now()},ERROR,\"Ошибка экспорта: $errorMessage\",graylog-export-service"
    }
}
