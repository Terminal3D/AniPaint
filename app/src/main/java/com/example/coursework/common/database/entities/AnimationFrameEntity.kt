package com.example.coursework.common.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "animation_frames",
    foreignKeys = [
        ForeignKey(
            entity = AnimationEntity::class,
            parentColumns = ["id"],
            childColumns = ["animationId"],
            onDelete = ForeignKey.CASCADE  // Каскадное удаление фреймов при удалении анимации
        )
    ]
)
data class AnimationFrameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val animationId: Int,  // Ссылка на анимацию
    val frameNumber: Int,  // Порядковый номер фрейма
    val frameDuration: Int,  // Продолжительность показа фрейма
    val image: ByteArray  // Изображение для фрейма
)