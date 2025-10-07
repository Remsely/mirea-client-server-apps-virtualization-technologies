package edu.mirea.remsely.csavt.practice3.mirealogodownloader.mapper

import edu.mirea.remsely.csavt.practice3.mirealogodownloader.dto.ItemDto
import edu.mirea.remsely.csavt.practice3.mirealogodownloader.entity.Item

fun Item.toDto() = ItemDto(
    id = id,
    name = name
)

fun List<Item>.toDto() = this.map { it.toDto() }
