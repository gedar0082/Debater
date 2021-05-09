package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class ArgumentJson (
    @JsonProperty("id") val id: Long,
    @JsonProperty("statement") val statement: String,
    @JsonProperty("clarification") val clarification: String?,
    @JsonProperty("evidence") val evidence: String?,
    @JsonProperty("summary") val summary: String?,
    @JsonProperty("answer") val answer_id: ArgumentJson?,
    @JsonProperty("debate_id") val debate_id: DebateJson?,
    @JsonProperty("thesis_id") val thesis_id: ThesisJson?,
    @JsonProperty("person_id") val person_id: PersonJson?,
    @JsonProperty("date_time") val date_time: Timestamp?
){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}