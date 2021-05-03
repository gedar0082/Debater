package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class PersonJson (
    @JsonProperty("id") val id: Long,
    @JsonProperty("nickname") val nickname: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("password") val password: String
)