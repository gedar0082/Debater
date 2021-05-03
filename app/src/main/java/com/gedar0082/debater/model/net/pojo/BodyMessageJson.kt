package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class BodyMessageJson(
    @JsonProperty("message") val message: String
)
