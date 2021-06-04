package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class ThesisJson (
    @JsonProperty("id") val id: Long,
    @JsonProperty("title") val title: String,
    @JsonProperty("shrt") val shrt: String,
    @JsonProperty("statement") val statement: String,
    @JsonProperty("round_number") val roundNumber: Long,
    @JsonProperty("answer_id") val answer: ThesisJson?,
    @JsonProperty("debate_id") val debate: DebateJson?,
    @JsonProperty("person_id") val person: PersonJson?,
    @JsonProperty("date_time") val dateTime: Timestamp?,
    @JsonProperty("type") val type: Int?
)