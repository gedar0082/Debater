package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class ArgumentJsonRaw (
    @JsonProperty("id") val id: Long,
    @JsonProperty("title") val title: String,
    @JsonProperty("statement") val statement: String,
    @JsonProperty("answer_id") val answer_id: Long?,
    @JsonProperty("debate_id") val debate_id: Long?,
    @JsonProperty("thesis_id") val thesis_id: Long?,
    @JsonProperty("person_id") val person_id: Long?,
    @JsonProperty("date_time") val date_time: Timestamp?,
    @JsonProperty("type") val type : Int?
)