package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class RegulationsJson (
    @JsonProperty("id") val id: Long,
    @JsonProperty("ruleType") val ruleType: Int
)