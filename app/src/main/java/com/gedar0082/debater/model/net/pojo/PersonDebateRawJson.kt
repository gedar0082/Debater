package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class PersonDebateRawJson(
    @JsonProperty val debateId: Long,
    @JsonProperty val personId: Long,
    @JsonProperty val rightsId: Long
)
