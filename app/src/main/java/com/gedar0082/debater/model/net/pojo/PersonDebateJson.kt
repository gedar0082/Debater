package com.gedar0082.debater.model.net.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class PersonDebateJson (
    @JsonProperty val debate: DebateJson,
    @JsonProperty val person: PersonJson,
    @JsonProperty val rights: RightJson
    )