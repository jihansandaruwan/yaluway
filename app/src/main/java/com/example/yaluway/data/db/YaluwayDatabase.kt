package com.example.yaluway.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, PostEntity::class, SavedPostEntity::class, RequestEntity::class],
    version = 2,
    exportSchema = false
)
abstract class YaluwayDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun savedPostDao(): SavedPostDao
    abstract fun requestDao(): RequestDao

    companion object {
        @Volatile
        private var instance: YaluwayDatabase? = null

        fun get(context: Context): YaluwayDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    YaluwayDatabase::class.java,
                    "yaluway.db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
