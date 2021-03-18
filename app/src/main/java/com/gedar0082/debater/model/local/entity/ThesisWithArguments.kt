package com.gedar0082.debater.model.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ThesisWithArguments(
    @Embedded val thesis: Thesis,
    @Relation(
        parentColumn = "thesis_id",
        entityColumn = "t_id"
    )
    val arguments : List<Argument>
)