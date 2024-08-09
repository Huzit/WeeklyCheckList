package com.weekly.weeklychecklist.di

import android.content.Context
import androidx.room.Room
import com.weekly.weeklychecklist.database.CheckListDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = Room
        .databaseBuilder(context, CheckListDatabase::class.java, "CheckListDatabase").build()

    @Provides
    fun provideCheckListDao(database: CheckListDatabase) = database.checkListDao()

    @Provides
    fun provideCheckListUpdate(database: CheckListDatabase) = database.checkListUpdateDao()
}