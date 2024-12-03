package com.example.coursework.common.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.coursework.common.database.entities.AnimationEntity
import com.example.coursework.common.database.entities.AnimationFrameEntity
import com.example.coursework.common.database.entities.LastImageEntity
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

    @Insert
    suspend fun insertLastImage(image: LastImageEntity)

    @Query("SELECT * FROM last_image")
    fun getLastImage() : Flow<LastImageEntity?>


    // Saved Images Operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSavedImage(image: SavedImageEntity): Long

    @Delete
    suspend fun deleteSavedImage(image: SavedImageEntity)

    @Query("SELECT * FROM saved_images")
    fun getSavedImages() : Flow<List<SavedImageEntity>>

    @Query("SELECT * FROM saved_images WHERE id = :id LIMIT 1" )
    fun getImageById(id: Int) : SavedImageEntity

    @Query("DELETE FROM saved_images WHERE id = :id")
    fun deleteImageById(id: Int)


    // Animations Operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimation(animation: AnimationEntity)

    @Insert
    suspend fun insertAnimationFrame(frame: AnimationFrameEntity)

    @Query("SELECT * FROM animations")
    fun getAllAnimations(): Flow<List<AnimationEntity>>

    @Query("SELECT * FROM animations WHERE id = :id LIMIT 1")
    fun getAnimationById(id: Int): Flow<AnimationEntity>

    @Delete
    suspend fun deleteAnimation(animation: AnimationEntity)

    @Query("DELETE FROM animations WHERE id = :id")
    suspend fun deleteAnimationById(id: Int)

    @Query("DELETE FROM animation_frames WHERE animationId = :animationId")
    suspend fun deleteAnimationFramesByAnimationId(animationId: Int)

}