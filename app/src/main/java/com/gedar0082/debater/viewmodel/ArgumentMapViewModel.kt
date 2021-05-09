package com.gedar0082.debater.viewmodel

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.databinding.Bindable
import androidx.lifecycle.ViewModel
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gedar0082.debater.R
import com.gedar0082.debater.model.net.api.ApiFactory
import com.gedar0082.debater.model.net.api.NotificationObject
import com.gedar0082.debater.model.net.pojo.ArgumentJson
import com.gedar0082.debater.model.net.pojo.ArgumentJsonRaw
import com.gedar0082.debater.util.CurrentUser
import com.gedar0082.debater.util.Util
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class ArgumentMapViewModel : ViewModel(), Observable, CoroutineScope {

    lateinit var context: Context
    var arguments = MutableLiveData<List<ArgumentJson>>()
    var debateId: Long = 0
    var thesisId: Long = 0
    @Bindable
    val argumentText = MutableLiveData<String>()


    override val coroutineContext: CoroutineContext
        get() = Job()

    private val apiFactory = ApiFactory.service
    private val notificationObject = NotificationObject.service

    fun createNewArgument(argument: ArgumentJson?){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.name_fields, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)
            .setPositiveButton("Create"){ dialog, _ ->
                run{
                    val text1: EditText? = promptView.findViewById(R.id.disName)
                    argumentText.value = text1?.text.toString()
                    val newArgument = ArgumentJsonRaw(0, argumentText.value!!, "", "","", argument!!.id, debateId, thesisId, CurrentUser.id, Util.getCurrentDate())
                    if (argument.id == Long.MAX_VALUE) saveArgumentWithoutAnswer(newArgument) else saveArgument(newArgument)
                    getArguments(debateId)
                    dialog.cancel()

                }
            }
        confirm.create()
        confirm.show()
    }

    fun getArguments(id: Long){
        launch {
            runCatching { apiFactory.getArgumentsByDebateId(id) }.onSuccess {
                println(it.toString())
                arguments.postValue(it)
            }.onFailure { it.printStackTrace() }
        }
    }

    private fun saveArgument(argumentJsonRaw: ArgumentJsonRaw): Long = runBlocking{
        saveArgumentAsync(argumentJsonRaw).await()
    }

    private fun saveArgumentAsync(argumentJsonRaw: ArgumentJsonRaw) = async {
        return@async apiFactory.insertArgumentRaw(argumentJsonRaw)
    }

    private fun saveArgumentWithoutAnswer(argumentJsonRaw: ArgumentJsonRaw): Long = runBlocking {
        saveArgumentWithoutAnswerAsync(argumentJsonRaw).await()
    }

    private fun saveArgumentWithoutAnswerAsync(argumentJsonRaw: ArgumentJsonRaw) = async {
        return@async apiFactory.insertArgumentWithoutAnswerRaw(argumentJsonRaw)
    }













    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }


}