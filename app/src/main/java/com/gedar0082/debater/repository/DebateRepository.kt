package com.gedar0082.debater.repository

import com.gedar0082.debater.model.local.entity.Debate
import com.gedar0082.debater.model.local.entity.DebateWithTheses
import com.gedar0082.debater.model.local.entityDao.DebateDao

class DebateRepository(private val debateDao: DebateDao) {

    val debates = debateDao.getAll()


    suspend fun insert(debate: Debate) {
        debateDao.insert(debate)
    }

    suspend fun update(debate: Debate) {
        debateDao.update(debate)
    }

    suspend fun delete(debate: Debate) {
        debateDao.delete(debate)
    }

    suspend fun deleteAll() {
        debateDao.deleteAll()
    }

    suspend fun getDebateWithTheses(id: Long): List<DebateWithTheses> {
        return debateDao.getDebateWithTheses(id)
    }

}