package com.example.coursework.features.paint

import com.example.coursework.features.paint.data.PaintRepository
import com.example.coursework.features.paint.data.PaintRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PaintModule {

    @Binds
    abstract fun bindPaintRepository(
        paintRepositoryImpl: PaintRepositoryImpl
    ): PaintRepository
}