package edu.mirea.remsely.csavt.practice3.mirealogodownloader.controller

import edu.mirea.remsely.csavt.practice3.mirealogodownloader.service.MireaLogoProvider
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mirea/logo")
class LogoController(
    private val logoProvider: MireaLogoProvider
) {
    @GetMapping(produces = [MediaType.IMAGE_PNG_VALUE])
    fun getCrestImage(): ResponseEntity<ByteArray> =
        logoProvider.getLogo().let { logoBytes ->
            if (logoBytes != null) {
                ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(logoBytes)
            } else {
                ResponseEntity.notFound().build()
            }
        }
}
