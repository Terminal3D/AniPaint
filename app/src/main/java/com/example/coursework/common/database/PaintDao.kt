package com.example.coursework.common.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.coursework.common.database.dto.LastImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaintDao {

    // Last Image Operations

    @Upsert
    suspend fun upsertLastImage(image: LastImageEntity)

    @Delete
    suspend fun deleteLastImage(image: LastImageEntity)

    @Query("SELECT * FROM last_image WHERE id = 0")
    fun getLastImage() : Flow<LastImageEntity?>

    // Saved Images Operations

}