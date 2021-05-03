package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Date
import java.sql.Timestamp

data class DebateJson (
    @JsonProperty("id") val id: Long,
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("dateStart") val dateStart: Timestamp,
    @JsonProperty("regulations") val regulations: RegulationsJson
)