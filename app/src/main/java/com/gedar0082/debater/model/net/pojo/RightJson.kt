package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class RightJson (
    @JsonProperty("id") val id : Long,
    @JsonProperty("read") val read : Int,
    @JsonProperty("write") val write: Int,
    @JsonProperty("referee") val referee : Int,
    @JsonProperty("creator") val creator : Int
        )