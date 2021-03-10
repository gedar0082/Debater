package com.gedar0082.debater.model.local.entityDao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gedar0082.debater.model.local.entity.Debate
import com.gedar0082.debater.model.local.entity.DebateWithTheses

@Dao
interface DebateDao {
    
    @Insert
    suspend fun insert(debate: Debate)
    
    @Update
    suspend fun update(debate: Debate)
    
    @Delete
    suspend fun delete(debate: Debate)
    
    @Query("DELETE FROM debate_table")
    suspend fun deleteAll()
    
    @Query("SELECT * FROM debate_table")
    fun getAll(): LiveData<List<Debate>>

//    @Transaction
//    @Query("SELECT * FROM debate_table WHERE debate_id = :id")
//    fun getDebateWithTheses(id: Long) : LiveData<List<DebateWithTheses>>

    @Transaction
    @Query("SELECT * FROM debate_table WHERE debate_id = :id")
    suspend fun getDebateWithTheses(id: Long) : List<DebateWithTheses>

}