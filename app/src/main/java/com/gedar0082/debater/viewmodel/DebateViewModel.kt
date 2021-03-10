package com.gedar0082.debater.viewmodel

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.databinding.Observable
import androidx.lifecycle.viewModelScope
import com.gedar0082.debater.R
import com.gedar0082.debater.model.local.entity.Debate
import com.gedar0082.debater.model.local.entity.Thesis
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class DebateViewModel(private val debateRepository : DebateRepository, private val trepo: ThesisRepository) : ViewModel(), Observable {

    val debates = debateRepository.debates
    lateinit var context: Context



    @Bindable val debateName = MutableLiveData<String>()
    @Bindable val debateDescription = MutableLiveData<String>()

    fun insert(): Job = viewModelScope.launch {
        val name: String = debateName.value ?: "discussion"
        val description : String = debateDescription.value ?: "description"
        debateRepository.insert(Debate(0, name, description))
    }

    fun update(debate: Debate): Job = viewModelScope.launch { debateRepository.update(debate) }

    fun delete(debate: Debate): Job = viewModelScope.launch { debateRepository.delete(debate) }

    fun deleteAll() : Job = viewModelScope.launch { debateRepository.deleteAll() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {}

    fun createNewDebate(){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.name_fields, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)
            .setPositiveButton("Create") { dialog, _ ->          //проверить, что будет, если убрать интерфейс и вытащить из параметров лямбду
                run {
                    val text1: EditText? = promptView.findViewById(R.id.disName)
                    val text2: EditText? = promptView.findViewById(R.id.disDescription)
                    debateName.value = text1?.text.toString()
                    debateDescription.value = text2?.text.toString()
                    insert()
                    dialog.cancel()
                }
            }
        confirm.create()
        confirm.show()
    }

    fun thesisInserter(debate: Debate):Job = viewModelScope.launch{
        trepo.insert(Thesis(0, debate.id, "suuka", "blyyat"))
    }

}