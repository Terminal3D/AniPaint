package com.example.coursework.features.animator

import com.example.coursework.features.animator.data.AnimatorRepository
import com.example.coursework.features.animator.data.AnimatorRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AnimatorModule {
    @Binds
    abstract fun bindAnimatorRepository(
        animatorRepositoryImpl: AnimatorRepositoryImpl
    ): AnimatorRepository
}