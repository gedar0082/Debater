package com.gedar0082.debater.model.net.pojo

data class PersonRightsJson (
    val person: PersonJson,
    val rights: RightJson
){
    override fun toString(): String {
        return "\n $person \n $rights"
    }
}