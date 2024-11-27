package com.example.coursework.features.entry

import com.example.coursework.features.entry.data.EntryRepository
import com.example.coursework.features.entry.data.EntryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class EntryModule {
    @Binds
    abstract fun bindEntryRepository(
        entryRepositoryImpl: EntryRepositoryImpl
    ): EntryRepository
}