package com.example.coursework.common.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_images")
data class SavedImageEntity(
    val image: ByteArray,
    val size: Int,
    val name: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

