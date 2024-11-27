package com.example.coursework.common.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_image")
data class LastImageEntity(
    @PrimaryKey val id: Int = 0,
    val image: List<IntArray>,
    val size: Int,
)
