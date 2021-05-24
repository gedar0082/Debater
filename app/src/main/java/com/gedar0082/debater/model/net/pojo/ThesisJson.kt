package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

data class ThesisJson (
    @JsonProperty("id") val id: Long,
    @JsonProperty("intro") val intro: String,
    @JsonProperty("definition") val definition: String?,
    @JsonProperty("problem") val problem: String?,
    @JsonProperty("plan") val plan: String?,
    @JsonProperty("case_intro") val caseIntro: String?,
    @JsonProperty("case_desc") val caseDesc: String?,
    @JsonProperty("round_number") val roundNumber: Long,
    @JsonProperty("answer_id") val answer: ThesisJson?,
    @JsonProperty("debate_id") val debate: DebateJson?,
    @JsonProperty("person_id") val person: PersonJson?,
    @JsonProperty("date_time") val dateTime: Timestamp?,
    @JsonProperty("type") val type: Int?
)