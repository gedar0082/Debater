package com.gedar0082.debater.model.local.entityDao

import androidx.lifecycle.LiveData
import androidx.room.*
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