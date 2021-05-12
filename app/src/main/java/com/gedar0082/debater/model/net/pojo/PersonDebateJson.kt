package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class PersonDebateJson (
    @JsonProperty("debate") val debate: DebateJson,
    @JsonProperty("person") val person: PersonJson,
    @JsonProperty("rights") val rights: RightJson
    )