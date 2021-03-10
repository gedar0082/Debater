package com.gedar0082.debater.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import com.gedar0082.debater.viewmodel.ThesisMapViewModel
import java.lang.IllegalArgumentException

class ThesisMapFactory (private val thesisRepository: ThesisRepository,
                        private val debateRepository: DebateRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThesisMapViewModel::class.java)){
            return ThesisMapViewModel(thesisRepository, debateRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}