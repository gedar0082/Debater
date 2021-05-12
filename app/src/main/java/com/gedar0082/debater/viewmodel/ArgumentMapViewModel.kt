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
import androidx.navigation.NavController
import com.gedar0082.debater.R
import com.gedar0082.debater.model.net.api.ApiFactory
import com.gedar0082.debater.model.net.api.NotificationObject
import com.gedar0082.debater.model.net.pojo.ArgumentJson
import com.gedar0082.debater.model.net.pojo.ArgumentJsonRaw
import com.gedar0082.debater.util.ArgumentList
import com.gedar0082.debater.util.CurrentUser
import com.gedar0082.debater.util.InterScreenController
import com.gedar0082.debater.util.Util
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

// не настроены оповещения!!! для аргументов.
class ArgumentMapViewModel : ViewModel(), Observable, CoroutineScope {

    lateinit var context: Context
    var arguments = MutableLiveData<List<ArgumentJson>>()
    var debateId: Long = 0
    var thesisId: Long = 0
    lateinit var navController: NavController
    @Bindable
    val argumentText = MutableLiveData<String>()


    override val coroutineContext: CoroutineContext
        get() = Job()

    private val apiFactory = ApiFactory.service
    private val notificationObject = NotificationObject.service

    fun createNewArgument(argument: ArgumentJson?){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.dialog_new_argument, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)

        val statement = promptView.findViewById<EditText>(R.id.argument_statement_input)
        val clarification = promptView.findViewById<EditText>(R.id.argument_clarification_input)
        val evidence = promptView.findViewById<EditText>(R.id.argument_evidence_input)
        val summary = promptView.findViewById<EditText>(R.id.argument_summary_input)

        confirm.setPositiveButton("Create"){ dialog, _ ->
                run{

                    if (InterScreenController.chooseAnswerArg == 1){

                        ArgumentList.argumentList.add(ArgumentJsonRaw(0, statement.text.toString(),
                            clarification.text.toString(),
                            evidence.text.toString(),
                            summary.text.toString(),
                            argument!!.id, debateId, thesisId, CurrentUser.id, Util.getCurrentDate()))
                        dialog.cancel()
                        InterScreenController.chooseAnswerArg = 2
                        navController.popBackStack()
                    }else{
                        argumentText.value = statement.text.toString()
                        val newArgument = ArgumentJsonRaw(0, argumentText.value!!, "", "","", argument!!.id, debateId, thesisId, CurrentUser.id, Util.getCurrentDate()) // в свободных дебатах thesisId выбрасывает nullpointer
                        if (argument.id == Long.MAX_VALUE) saveArgumentWithoutAnswer(newArgument) else saveArgument(newArgument)
                        getArguments(debateId)
                        dialog.cancel()
                    }
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