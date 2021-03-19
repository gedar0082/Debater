package com.gedar0082.debater.viewmodel

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.databinding.Bindable
import androidx.lifecycle.ViewModel
import com.gedar0082.debater.repository.ArgumentRepository
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gedar0082.debater.R
import com.gedar0082.debater.model.local.entity.Argument
import com.gedar0082.debater.model.local.entity.DebateWithArguments
import kotlinx.coroutines.*


class ArgumentMapViewModel( private val drepo : DebateRepository,
                            private val trepo : ThesisRepository,
                            private val arepo: ArgumentRepository) : ViewModel(), Observable {

    lateinit var context: Context
    var arguments = MutableLiveData<List<DebateWithArguments>>()
    var debateId: Long = 0

    @Bindable
    val argumentText = MutableLiveData<String>()

    init {
        getArguments(debateId)
    }

    fun getArguments(id: Long){
        GlobalScope.launch(Dispatchers.IO) {
            val args = async(Dispatchers.IO) {
                return@async drepo.getDebateWithArguments(id)
            }.await()
            withContext(Dispatchers.Main){
                arguments.value = args
            }
        }
        if (arguments.value != null){
            println("arguments size from getArguments = ${arguments.value!!.first().arguments.size}")
        }

    }

    fun createNewArgument(argument: Argument?){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.name_fields, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)
            .setPositiveButton("Create"){ dialog, _ ->
                run{
                    val text1: EditText? = promptView.findViewById(R.id.disName)
                    argumentText.value = text1?.text.toString()
                    insertArgument(Argument(0,argument!!.aId,0,debateId,argumentText.value!!))
                    dialog.cancel()

                }
            }
        confirm.create()
        confirm.show()
    }

    fun insertArgument(argument: Argument): Job = viewModelScope.launch{
        arepo.insert(argument)
        getArguments(debateId)
    }









    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }


}