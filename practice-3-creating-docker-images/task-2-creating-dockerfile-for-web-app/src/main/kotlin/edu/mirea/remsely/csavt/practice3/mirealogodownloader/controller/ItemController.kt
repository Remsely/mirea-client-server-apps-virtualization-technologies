package edu.mirea.remsely.csavt.practice3.mirealogodownloader.controller

import edu.mirea.remsely.csavt.practice3.mirealogodownloader.dto.ItemDto
import edu.mirea.remsely.csavt.practice3.mirealogodownloader.service.ItemService
import edu.mirea.remsely.csavt.practice3.mirealogodownloader.service.MireaLogoProvider
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class ItemController(
    private val itemService: ItemService,
    private val logoProvider: MireaLogoProvider,
) {
    @PostMapping("/items")
    fun addItem(@RequestBody item: ItemDto): ItemDto = itemService.saveItem(item)

    @GetMapping("/items")
    fun getAllItems(): List<ItemDto> = itemService.getAllItems()

    @GetMapping("/mirea/logo", produces = [MediaType.IMAGE_PNG_VALUE])
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
