package edu.mirea.remsely.csavt.practice3.mirealogodownloader.repository

import edu.mirea.remsely.csavt.practice3.mirealogodownloader.entity.Item
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository : JpaRepository<Item, Long>
