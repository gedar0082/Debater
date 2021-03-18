package com.gedar0082.debater.viewmodel


import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gedar0082.debater.R
import com.gedar0082.debater.model.local.entity.DebateWithTheses
import com.gedar0082.debater.model.local.entity.Thesis
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import kotlinx.coroutines.*


class ThesisMapViewModel(
    private val thesisRepository: ThesisRepository,
    private val debateRepository: DebateRepository
) : ViewModel(), Observable {

//    lateinit var clickableThesis: Thesis

    lateinit var context: Context
    var theses = MutableLiveData<List<DebateWithTheses>>()
    var debateId: Long = 0

    @Bindable
    val thesisName = MutableLiveData<String>()
    @Bindable
    val thesisDesc = MutableLiveData<String>()

    init {
        getTheses(debateId)
    }


    fun getTheses(id: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            val the = async(Dispatchers.IO) {
                return@async debateRepository.getDebateWithTheses(id)
            }.await()
            withContext(Dispatchers.Main) {
                theses.value = the
            }
        }
    }


    fun createNewThesis(thesis: Thesis?) {
        val thesiss: Thesis
        if (thesis == null){
            thesiss = Thesis(0, debateId, 0, thesisName.value!!, thesisDesc.value!!, null, null, null, null, null)
        }else{
            thesiss = thesis
        }
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.name_fields, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)
            .setPositiveButton("Create") { dialog, _ ->
                run {
                    val text1: EditText? = promptView.findViewById(R.id.disName)
                    val text2: EditText? = promptView.findViewById(R.id.disDescription)
                    thesisName.value = text1?.text.toString()
                    thesisDesc.value = text2?.text.toString()
                    insertThesis(thesiss)
                    dialog.cancel()
                }
            }
        confirm.create()
        confirm.show()
    }



    private fun insertThesis(thesis: Thesis): Job = viewModelScope.launch {
        thesisRepository.insert(Thesis(0, debateId, thesis.tId, thesisName.value!!, thesisDesc.value!!, null, null, null, null, null))
        getTheses(debateId)
    }


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}