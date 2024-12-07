package com.example.coursework.features.animator.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface AnimatorRepository {
    suspend fun saveAnimation(animationData: AnimationData, context: Context) : Int
    suspend fun getAnimation(id : Int) : AnimationData
    suspend fun getImages() : Flow<List<AnimationImageData>>
}