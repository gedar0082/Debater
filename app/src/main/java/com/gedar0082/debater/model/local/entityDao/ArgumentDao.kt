package com.gedar0082.debater.model.local.entityDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.gedar0082.debater.model.local.entity.Argument

@Dao
interface ArgumentDao {

    @Insert
    suspend fun insert(argument: Argument)

    @Update
    suspend fun update(argument: Argument)

    @Delete
    suspend fun delete(argument: Argument)
}