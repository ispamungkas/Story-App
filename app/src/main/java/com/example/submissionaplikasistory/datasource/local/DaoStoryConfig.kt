package com.example.submissionaplikasistory.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EntityDaoStory::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class DaoStoryConfig : RoomDatabase() {
    abstract fun getService(): DaoService
    abstract fun remoteKeyDao(): RemoteKeysDao

    companion object {
        private var instance: DaoStoryConfig? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                DaoStoryConfig::class.java,
                "Story Config Database"
            ).fallbackToDestructiveMigration().build()
                .also { instance = it }
        }
    }
}