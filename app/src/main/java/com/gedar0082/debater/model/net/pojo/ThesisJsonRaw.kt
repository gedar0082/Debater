package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class ThesisJsonRaw (
    @JsonProperty("id") val id: Long,
    @JsonProperty("title") val title: String,
    @JsonProperty("shrt") val shrt: String,
    @JsonProperty("statement") val statement: String,
    @JsonProperty("round_number") val round_number: Int,
    @JsonProperty("answer_id") val answer_id: Long?,
    @JsonProperty("debate_id") val debate_id: Long,
    @JsonProperty("person_id") val person_id: Long,
    @JsonProperty("date_time") val date_time: Timestamp,
    @JsonProperty("type") val type: Int?
)