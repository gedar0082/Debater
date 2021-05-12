package com.gedar0082.debater.model.net.pojo

data class DebateWithPersons(
    val debate: DebateJson,
    val personsWithRights: List<PersonRightsJson>
){
    override fun toString(): String {
        return "\n List of DebateWithPerson =  $debate \n $personsWithRights"
    }

    public fun findCreator(): PersonJson?{
        personsWithRights.forEach {
            if(it.rights.creator == 1) return it.person
        }
        return null
    }
}
