package com.example.coursework.common.database.di

import android.app.Application
import androidx.room.Room
import com.example.coursework.common.database.PaintDao
import com.example.coursework.common.database.PaintDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaintDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): PaintDatabase {
        return Room.databaseBuilder(
            app,
            PaintDatabase::class.java,
            "paint_database"
        )
            .fallbackToDestructiveMigration()
            .build()

    }

    @Provides
    fun provideImageDao(database: PaintDatabase): PaintDao {
        return database.paintDao
    }

    @Provides
    @Singleton
    fun providePaintDatabaseRepository(paintDao: PaintDao): PaintDatabaseRepository {
        return PaintDatabaseRepositoryImpl(paintDao)
    }
}