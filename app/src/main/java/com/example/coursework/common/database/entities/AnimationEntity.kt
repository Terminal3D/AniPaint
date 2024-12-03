package com.example.coursework.common.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animations")
data class AnimationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val frameCount: Int, // Суммарное количество фреймов для анимации
)
