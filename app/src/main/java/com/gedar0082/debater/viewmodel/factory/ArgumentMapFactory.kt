package com.gedar0082.debater.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gedar0082.debater.repository.ArgumentRepository
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import com.gedar0082.debater.viewmodel.ArgumentMapViewModel
import java.lang.IllegalArgumentException

class ArgumentMapFactory (private val repository: DebateRepository,
                          private val trepo: ThesisRepository,
                          private val arepo: ArgumentRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArgumentMapViewModel::class.java)){
            return ArgumentMapViewModel(repository, trepo, arepo) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}