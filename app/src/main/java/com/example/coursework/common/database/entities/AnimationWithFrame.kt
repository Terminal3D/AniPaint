package com.example.coursework.common.database.entities

import androidx.room.Embedded

data class AnimationWithFrame(
    @Embedded val animation: AnimationEntity,
    @Embedded val frame: List<AnimationFrameEntity>
)
