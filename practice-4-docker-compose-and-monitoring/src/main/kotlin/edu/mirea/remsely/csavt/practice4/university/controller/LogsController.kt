package edu.mirea.remsely.csavt.practice4.university.controller

import edu.mirea.remsely.csavt.practice4.university.service.GraylogExportService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/logs")
class LogsController(
    private val graylogExportService: GraylogExportService
) {
    @GetMapping("/export", produces = ["text/csv"])
    fun exportLogs(
        @RequestParam(required = false) query: String? = null,
        @RequestParam(required = false) range: Int? = 300
    ): ResponseEntity<String> {
        val csvData = graylogExportService.exportLogsAsCsv(query, range)

        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val filename = "logs_export_$timestamp.csv"

        val headers = HttpHeaders().apply {
            contentType = MediaType.parseMediaType("text/csv")
            setContentDispositionFormData("attachment", filename)
        }

        return ResponseEntity.ok()
            .headers(headers)
            .body(csvData)
    }
}
