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

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + statement.hashCode()
        result = 31 * result + (clarification?.hashCode() ?: 0)
        result = 31 * result + (evidence?.hashCode() ?: 0)
        result = 31 * result + (summary?.hashCode() ?: 0)
        result = 31 * result + (answer_id?.hashCode() ?: 0)
        result = 31 * result + (debate_id?.hashCode() ?: 0)
        result = 31 * result + (thesis_id?.hashCode() ?: 0)
        result = 31 * result + (person_id?.hashCode() ?: 0)
        result = 31 * result + (date_time?.hashCode() ?: 0)
        return result
    }
}