package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class ThesisJsonRaw (
    @JsonProperty("id") val id: Long,
    @JsonProperty("intro") val intro: String,
    @JsonProperty("definition") val definition: String?,
    @JsonProperty("problem") val problem: String?,
    @JsonProperty("plan") val plan: String?,
    @JsonProperty("case_intro") val case_intro: String?,
    @JsonProperty("case_desc") val case_desc: String?,
    @JsonProperty("idea") val idea: String?,
    @JsonProperty("round_number") val round_number: Int,
    @JsonProperty("answer_id") val answer_id: Long?,
    @JsonProperty("debate_id") val debate_id: Long,
    @JsonProperty("person_id") val person_id: Long,
    @JsonProperty("date_time") val date_time: Timestamp,
    @JsonProperty("type") val type: Int?
)