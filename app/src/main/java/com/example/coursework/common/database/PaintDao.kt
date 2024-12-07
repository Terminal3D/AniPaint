package com.example.coursework.common.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.coursework.common.database.entities.AnimationEntity
import com.example.coursework.common.database.entities.AnimationFrameEntity
import com.example.coursework.common.database.entities.LastImageEntity
import com.example.coursework.common.database.entities.SavedAnimationEntity
import com.example.coursework.common.database.entities.SavedImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaintDao {

    // Last Image Operations


    suspend fun upsertLastImage(image: LastImageEntity) {
        deleteLastImage()
        insertLastImage(image)
    }

    @Transaction
    @Query("DELETE FROM last_image")
    suspend fun deleteLastImage()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastImage(image: LastImageEntity)

    @Query("SELECT * FROM last_image")
    fun getLastImage() : Flow<LastImageEntity?>


    // Saved Images Operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSavedImage(image: SavedImageEntity): Long

    @Delete
    suspend fun deleteSavedImage(image: SavedImageEntity)

    @Query("SELECT * FROM saved_images ORDER BY id DESC")
    fun getSavedImages() : Flow<List<SavedImageEntity>>

    @Query("SELECT * FROM saved_images WHERE id = :id LIMIT 1" )
    fun getImageById(id: Int) : SavedImageEntity

    @Query("DELETE FROM saved_images WHERE id = :id")
    fun deleteImageById(id: Int)


    // Animations Operations

    @Upsert
    suspend fun upsertAnimation(animation: AnimationEntity) : Long

    @Insert
    suspend fun insertAnimationFrames(frames: List<AnimationFrameEntity>)

    @Query("SELECT * FROM animations ORDER BY id DESC")
    fun getAllAnimations(): Flow<List<AnimationEntity>>

    @Query("SELECT * FROM animations WHERE id = :id LIMIT 1")
    fun getAnimationById(id: Int): Flow<AnimationEntity>

    @Query("SELECT * FROM animation_frames WHERE animationId = :id ORDER BY frameNumber ASC")
    fun getAnimationFramesById(id: Int): Flow<List<AnimationFrameEntity>>

    @Delete
    suspend fun deleteAnimation(animation: AnimationEntity)

    @Query("DELETE FROM animations WHERE id = :id")
    suspend fun deleteAnimationById(id: Int)

    @Query("DELETE FROM animation_frames WHERE animationId = :animationId")
    suspend fun deleteAnimationFramesByAnimationId(animationId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedAnimation(savedAnimation: SavedAnimationEntity)

    @Delete
    suspend fun deleteSavedAnimation(savedAnimation: SavedAnimationEntity)

    @Query("SELECT * FROM saved_animations ORDER BY id DESC")
    fun getSavedAnimations(): Flow<List<SavedAnimationEntity>>

    @Query("SELECT * FROM saved_animations WHERE id = :animationId LIMIT 1")
    suspend fun getSavedAnimationById(animationId: Int): SavedAnimationEntity

    @Query("DELETE FROM saved_animations WHERE id = :animationId")
    suspend fun deleteSavedAnimationById(animationId: Int)
}