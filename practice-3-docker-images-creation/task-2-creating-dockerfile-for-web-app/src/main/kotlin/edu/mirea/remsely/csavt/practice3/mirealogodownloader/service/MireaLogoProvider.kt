package edu.mirea.remsely.csavt.practice3.mirealogodownloader.service

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

@Service
class MireaLogoProvider {
    fun getLogo(): ByteArray? {
        val resource = ClassPathResource("static/MIREA_Gerb_Colour.png")
        return if (resource.exists()) {
            resource.inputStream.readBytes()
        } else {
            null
        }
    }
}
