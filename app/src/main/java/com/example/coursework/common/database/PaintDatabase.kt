package com.example.coursework.common.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.coursework.common.database.converters.Converters
import com.example.coursework.common.database.entities.LastImageEntity
import com.example.coursework.common.database.entities.SavedImageEntity

@Database(
    entities = [LastImageEntity::class, SavedImageEntity::class],
    version = 2
)
abstract class PaintDatabase : RoomDatabase() {
    abstract val paintDao : PaintDao

}