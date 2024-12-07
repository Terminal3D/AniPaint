package com.example.coursework.common.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_animations")
data class SavedAnimationEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val uri: String,
)