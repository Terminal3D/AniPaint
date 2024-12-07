package com.example.coursework.features.gallery.data

import kotlinx.coroutines.flow.Flow

interface GalleryRepository {

    fun getSavedImages(): Flow<List<GalleryImage>>

    suspend fun deleteSavedImage(image: GalleryImage)

    suspend fun getSavedAnimations() : Flow<List<GalleryAnimation>>

    suspend fun deleteSavedAnimation(animation: GalleryAnimation)

}