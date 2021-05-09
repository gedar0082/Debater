package com.gedar0082.debater.view.adapters

import com.gedar0082.debater.model.net.pojo.DebateJson
import com.gedar0082.debater.model.net.pojo.PersonJson

data class DebatePersonContainer(
    val debate: DebateJson,
    val creator: PersonJson
)
