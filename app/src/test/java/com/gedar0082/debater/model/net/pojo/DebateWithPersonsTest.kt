package com.gedar0082.debater.model.net.pojo

import com.gedar0082.debater.util.Util
import org.junit.Test

import org.junit.Assert.*

class DebateWithPersonsTest {

    @Test
    fun findCreatorThatExists() {
        val regulationsJson = RegulationsJson(0, 1, 1)
        val debateJson = DebateJson(0, "name"," desc", Util.getCurrentDate(), regulationsJson)
        val personRightsJsonList = mutableListOf<PersonRightsJson>()
        personRightsJsonList.add(PersonRightsJson(PersonJson(0, "name0", "email0","psw0"), RightJson(0, 1, 1, 0, 0)))
        personRightsJsonList.add(PersonRightsJson(PersonJson(1, "name1", "email1","psw1"), RightJson(0, 1, 1, 0, 0)))
        personRightsJsonList.add(PersonRightsJson(PersonJson(2, "name2", "email2","psw2"), RightJson(0, 1, 1, 1, 1)))
        personRightsJsonList.add(PersonRightsJson(PersonJson(3, "name3", "email3","psw3"), RightJson(0, 1, 1, 0, 0)))
        val debateWithPersons = DebateWithPersons(debateJson, personRightsJsonList)
        val creator = debateWithPersons.findCreator()
        assertEquals(PersonJson(2, "name2", "email2","psw2"), creator)
    }

    @Test
    fun findCreatorThatNotExists() {
        val regulationsJson = RegulationsJson(0, 1, 1)
        val debateJson = DebateJson(0, "name"," desc", Util.getCurrentDate(), regulationsJson)
        val personRightsJsonList = mutableListOf<PersonRightsJson>()
        personRightsJsonList.add(PersonRightsJson(PersonJson(0, "name0", "email0","psw0"), RightJson(0, 1, 1, 0, 0)))
        personRightsJsonList.add(PersonRightsJson(PersonJson(1, "name1", "email1","psw1"), RightJson(0, 1, 1, 0, 0)))
        personRightsJsonList.add(PersonRightsJson(PersonJson(3, "name3", "email3","psw3"), RightJson(0, 1, 1, 0, 0)))
        val debateWithPersons = DebateWithPersons(debateJson, personRightsJsonList)
        val creator = debateWithPersons.findCreator()
        assertEquals(null, creator)
    }
}