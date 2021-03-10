package com.gedar0082.debater.model.local.entity

import androidx.room.Embedded
import androidx.room.Relation


data class DebateWithTheses (
    @Embedded val debate: Debate,
    @Relation(
        parentColumn = "debate_id",
        entityColumn = "d_id"
    )
    val theses: List<Thesis>
)