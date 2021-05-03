package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class PersonAuthenticationJson (
    @JsonProperty val nickname: String,
    @JsonProperty val email: String,
    @JsonProperty val password: String
    )