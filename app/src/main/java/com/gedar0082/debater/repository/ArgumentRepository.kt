package com.gedar0082.debater.repository

import com.gedar0082.debater.model.local.entity.Argument
import com.gedar0082.debater.model.local.entityDao.ArgumentDao

class ArgumentRepository(private val argumentDao: ArgumentDao) {

    suspend fun insert(argument : Argument) {argumentDao.insert(argument)}

    suspend fun update(argument : Argument) {argumentDao.update(argument)}

    suspend fun delete(argument : Argument) {argumentDao.delete(argument)}

}