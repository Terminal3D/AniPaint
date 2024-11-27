package com.example.coursework.features.paintMenu

import com.example.coursework.features.paintMenu.data.PaintMenuRepository
import com.example.coursework.features.paintMenu.data.PaintMenuRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PaintMenuModule {

    @Binds
    abstract fun bindPaintMenuRepository(
        paintMenuRepositoryImpl: PaintMenuRepositoryImpl
    ): PaintMenuRepository
}