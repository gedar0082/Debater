package com.gedar0082.debater.model.local.entityDao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gedar0082.debater.model.local.entity.Thesis
import com.gedar0082.debater.model.local.entity.ThesisWithArguments

@Dao
interface ThesisDao {

    @Insert
    suspend fun insert(thesis: Thesis)

    @Update
    suspend fun update(thesis: Thesis)

    @Delete
    suspend fun delete(thesis: Thesis)

    @Query("SELECT * FROM thesis_table WHERE d_id = :id")
    fun getTheses(id: Long): LiveData<List<Thesis>>

    @Transaction
    @Query("SELECT * FROM thesis_table WHERE thesis_id = :id")
    suspend fun getArgumentsByThesis(id: Long): List<ThesisWithArguments>
}