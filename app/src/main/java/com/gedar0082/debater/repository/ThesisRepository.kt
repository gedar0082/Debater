package com.gedar0082.debater.repository

import com.gedar0082.debater.model.local.entity.Thesis
import com.gedar0082.debater.model.local.entityDao.ThesisDao

class ThesisRepository(private val thesisDao: ThesisDao) {

    suspend fun insert(thesis: Thesis) { thesisDao.insert(thesis) }

    suspend fun update(thesis: Thesis) { thesisDao.update(thesis) }

    suspend fun delete(thesis: Thesis) { thesisDao.delete(thesis) }




}