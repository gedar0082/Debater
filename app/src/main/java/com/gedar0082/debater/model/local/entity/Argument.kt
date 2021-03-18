package com.gedar0082.debater.model.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "argument_table")
data class Argument (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "argument_id") val aId: Long,
    @ColumnInfo(name = "argument_answer_to") val answerTo: Long,
    @ColumnInfo(name = "t_id") val tId: Long,
    @ColumnInfo(name = "deb_id") val dId: Long,
    @ColumnInfo(name = "argument_text") val argText: String
)