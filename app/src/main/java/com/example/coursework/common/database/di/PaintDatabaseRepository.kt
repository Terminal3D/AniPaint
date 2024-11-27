package com.example.coursework.common.database.di

import com.example.coursework.core.models.PaintImage
import kotlinx.coroutines.flow.Flow

interface PaintDatabaseRepository {

    fun getLastImage() : Flow<PaintImage?>

    suspend fun updateLastImage(image: PaintImage)

}