package com.example.coursework.common.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_image")
data class LastImageEntity(
    @PrimaryKey val id: Int = 0,
    val name: String?,
    val image: ByteArray,
    val size: Int,
)
