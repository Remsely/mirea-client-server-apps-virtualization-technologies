package edu.mirea.remsely.csavt.practice3.mirealogodownloader.service

import edu.mirea.remsely.csavt.practice3.mirealogodownloader.dto.ItemDto
import edu.mirea.remsely.csavt.practice3.mirealogodownloader.mapper.toDto
import edu.mirea.remsely.csavt.practice3.mirealogodownloader.mapper.toEntity
import edu.mirea.remsely.csavt.practice3.mirealogodownloader.repository.ItemRepository
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository
) {
    fun saveItem(item: ItemDto): ItemDto = itemRepository.save(item.toEntity()).toDto()

    fun getAllItems(): List<ItemDto> = itemRepository.findAll().toDto()
}
