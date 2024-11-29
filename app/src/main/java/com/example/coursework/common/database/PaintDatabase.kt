package com.example.coursework.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coursework.common.database.entities.LastImageEntity
import com.example.coursework.common.database.entities.SavedImageEntity

@Database(
    entities = [LastImageEntity::class, SavedImageEntity::class],
    version = 9
)
abstract class PaintDatabase : RoomDatabase() {
    abstract val paintDao : PaintDao
}