package com.gedar0082.debater.model.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thesis_table")
data class Thesis (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "thesis_id") val tId: Long,
    @ColumnInfo(name = "d_id") val dId: Long,
    @ColumnInfo(name = "answer_to") val answerTo: Long,
    @ColumnInfo(name = "thesis_name") val thesisName: String,
    @ColumnInfo(name = "thesis_intro") val thesisIntro: String,
    @ColumnInfo(name = "thesis_definition") val thesisDefinition: String?,
    @ColumnInfo(name = "thesis_problem") val thesisProblem: String?,
    @ColumnInfo(name = "thesis_plan") val thesisPlan: String?,
    @ColumnInfo(name = "thesis_case") val thesisCase: String?,
    @ColumnInfo(name = "thesis_denial") val thesisDenial: String?
)