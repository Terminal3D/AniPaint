package com.example.coursework.features.animator.data

import android.content.Context
import com.example.coursework.common.database.di.PaintDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnimatorRepositoryImpl @Inject constructor(
    private val paintDatabaseRepository: PaintDatabaseRepository
) : AnimatorRepository {
    override suspend fun saveAnimation(animationData: AnimationData, context: Context): Int {
        return if (animationData.id == null) {
            val id = paintDatabaseRepository.insertAnimation(
                animationData.toAnimationEntity(),
                animationData.frames.map { it.toAnimationFrameEntity(animationId = 0, wipeId = true) }
            )
            animationData.copy(id = id).toSavedAnimationEntity(context)?.let {
                paintDatabaseRepository.insertSavedAnimation(it)
            }
            id
        } else {
            animationData.toSavedAnimationEntity(context)
            paintDatabaseRepository.updateAnimation(
                animationData.toAnimationEntity(),
                animationData.frames.map { it.toAnimationFrameEntity(animationId = animationData.id, wipeId = false) }
            )
        }
    }

    override suspend fun getAnimation(id: Int): AnimationData {
        val animationWithFrames = paintDatabaseRepository.getAnimationWithFramesById(id)
        val animation = animationWithFrames.first.first()
        val frames = animationWithFrames.second.first()
        return animation.toAnimationData(frames)
    }

    override suspend fun getImages(): Flow<List<AnimationImageData>> {
        return paintDatabaseRepository.getSavedImages().map { list -> list.map { it.toAnimationImageData() } }
    }
}