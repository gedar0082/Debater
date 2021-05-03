package com.gedar0082.debater.model.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "debate_table")
data class Debate(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "debate_id") val id: Long,
    @ColumnInfo(name = "debate_name") val name: String,
    @ColumnInfo(name = "debate_description") val description: String,
)