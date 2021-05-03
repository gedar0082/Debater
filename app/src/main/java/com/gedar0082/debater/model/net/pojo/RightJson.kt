package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class RightJson (
    @JsonProperty val id : Long,
    @JsonProperty val read : Int,
    @JsonProperty val write: Int,
    @JsonProperty val referee : Int,
    @JsonProperty val creator : Int
        )