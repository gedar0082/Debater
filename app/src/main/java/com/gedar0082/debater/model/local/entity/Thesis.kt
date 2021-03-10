package com.gedar0082.debater.model.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thesis_table")
data class Thesis (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "thesis_id") val tId: Long,
    @ColumnInfo(name = "d_id") val dId: Long,
    @ColumnInfo(name = "thesis_name") val thesisName: String,
    @ColumnInfo(name = "thesis_description") val thesisDesc: String
)