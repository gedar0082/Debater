package com.gedar0082.debater.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import com.gedar0082.debater.viewmodel.DebateViewModel
import java.lang.IllegalArgumentException

class DebateFactory (private val repository: DebateRepository, private val trepo: ThesisRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DebateViewModel::class.java)){
            return DebateViewModel(repository, trepo) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}