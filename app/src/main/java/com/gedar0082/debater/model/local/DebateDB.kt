package com.gedar0082.debater.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gedar0082.debater.model.local.entity.Argument
import com.gedar0082.debater.model.local.entity.Debate
import com.gedar0082.debater.model.local.entity.Thesis
import com.gedar0082.debater.model.local.entityDao.ArgumentDao
import com.gedar0082.debater.model.local.entityDao.DebateDao
import com.gedar0082.debater.model.local.entityDao.ThesisDao

@Database(entities = [Debate::class, Thesis::class, Argument::class], version = 3)
abstract class DebateDB : RoomDatabase() {

    abstract fun debateDao(): DebateDao
    abstract fun thesisDao(): ThesisDao
    abstract fun argumentDao(): ArgumentDao

    companion object {
        @Volatile
        private var INSTANCE: DebateDB? = null

        fun getDatabase(context: Context): DebateDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, DebateDB::class.java, "database")
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}