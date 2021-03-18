package com.gedar0082.debater.model.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DebateWithArguments (
    @Embedded val debate : Debate,
    @Relation(
        parentColumn = "debate_id",
        entityColumn = "deb_id"
    )
    val arguments : List<Argument>
)