package edu.mirea.remsely.csavt.practice3.mirealogodownloader.controller

import edu.mirea.remsely.csavt.practice3.mirealogodownloader.dto.ItemDto
import edu.mirea.remsely.csavt.practice3.mirealogodownloader.service.ItemService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/items")
class ItemController(
    private val itemService: ItemService,
) {
    @PostMapping
    fun addItem(@RequestBody item: ItemDto): ItemDto = itemService.saveItem(item)

    @GetMapping
    fun getAllItems(): List<ItemDto> = itemService.getAllItems()
}
