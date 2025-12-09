package edu.mirea.remsely.csavt.practice3.mirealogodownloader.mapper

import edu.mirea.remsely.csavt.practice3.mirealogodownloader.dto.ItemDto
import edu.mirea.remsely.csavt.practice3.mirealogodownloader.entity.Item

fun ItemDto.toEntity() = Item(
    id = id,
    name = name
)
