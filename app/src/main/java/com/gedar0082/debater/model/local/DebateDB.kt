package com.gedar0082.debater.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gedar0082.debater.model.local.entity.Debate
import com.gedar0082.debater.model.local.entity.Thesis
import com.gedar0082.debater.model.local.entityDao.DebateDao
import com.gedar0082.debater.model.local.entityDao.ThesisDao

@Database(entities = [Debate::class, Thesis::class], version = 1)
abstract class DebateDB : RoomDatabase() {

    abstract fun debateDao(): DebateDao
    abstract fun thesisDao(): ThesisDao

    companion object {
        @Volatile
        private var INSTANCE: DebateDB? = null

        fun getDatabase(context: Context): DebateDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, DebateDB::class.java, "database").build()
                INSTANCE = instance
                return instance
            }
        }
    }

}