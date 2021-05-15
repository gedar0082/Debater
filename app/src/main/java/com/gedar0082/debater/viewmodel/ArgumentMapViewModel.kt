package com.gedar0082.debater.viewmodel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.gedar0082.debater.R
import com.gedar0082.debater.model.net.api.ApiFactory
import com.gedar0082.debater.model.net.api.NotificationObject
import com.gedar0082.debater.model.net.notification.NotificationData
import com.gedar0082.debater.model.net.notification.PushNotification
import com.gedar0082.debater.model.net.pojo.ArgumentJson
import com.gedar0082.debater.model.net.pojo.ArgumentJsonRaw
import com.gedar0082.debater.util.ArgumentList
import com.gedar0082.debater.util.CurrentUser
import com.gedar0082.debater.util.InterScreenController
import com.gedar0082.debater.util.Util
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext


class ArgumentMapViewModel : ViewModel(), CoroutineScope {

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
    lateinit var navController: NavController
    lateinit var topic: String
    var arguments = MutableLiveData<List<ArgumentJson>>()
    var debateId: Long = 0
    var thesisId: Long = 0

    override val coroutineContext: CoroutineContext
        get() = Job()

    private val apiFactory = ApiFactory.service
    private val notificationObject = NotificationObject.service

    fun createNewArgument(argument: ArgumentJson?) {
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.dialog_new_argument, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)

        val statement = promptView.findViewById<EditText>(R.id.argument_statement_input)
        val clarification = promptView.findViewById<EditText>(R.id.argument_clarification_input)
        val evidence = promptView.findViewById<EditText>(R.id.argument_evidence_input)
        val summary = promptView.findViewById<EditText>(R.id.argument_summary_input)

        confirm.setPositiveButton("Create") { dialog, _ ->
            run {
                if (InterScreenController.chooseAnswerArg == 1) {
                    ArgumentList.argumentList.add(
                        ArgumentJsonRaw(
                            0, statement.text.toString(),
                            clarification.text.toString(),
                            evidence.text.toString(),
                            summary.text.toString(),
                            argument!!.id, debateId, thesisId, CurrentUser.id, Util.getCurrentDate()
                        )
                    )
                    dialog.cancel()
                    InterScreenController.chooseAnswerArg = 2
                    navController.popBackStack()
                } else {
                    val newArgument = ArgumentJsonRaw(
                        0,
                        statement.text.toString(),
                        clarification.text.toString(),
                        evidence.text.toString(),
                        summary.text.toString(),
                        argument!!.id, debateId,
                        null,
                        CurrentUser.id,
                        Util.getCurrentDate()
                    )
                    if (argument.id == Long.MAX_VALUE) saveArgumentWithoutAnswer(newArgument)
                    else saveArgument(newArgument)
                    getArguments(debateId)
                    dialog.cancel()
                }
            }
        }
        confirm.create()
        confirm.show()
    }

    fun openArgument(argument: ArgumentJson){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.argument_open, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)
        val statementText = promptView.findViewById<TextView>(R.id.arg_statement)
        val clarificationText = promptView.findViewById<TextView>(R.id.arg_clarification)
        val evidenceText = promptView.findViewById<TextView>(R.id.arg_evidence)
        val summaryText = promptView.findViewById<TextView>(R.id.arg_summary)
        statementText.text = argument.statement
        clarificationText.text = argument.clarification
        evidenceText.text = argument.evidence
        summaryText.text = argument.summary
        confirm.create()
        confirm.show()
    }

    fun getArguments(id: Long) {
        launch {
            runCatching { apiFactory.getArgumentsByDebateId(id) }.onSuccess {
                println(it.toString())
                arguments.postValue(it)
            }.onFailure { it.printStackTrace() }
        }
    }

    private fun saveArgument(argumentJsonRaw: ArgumentJsonRaw): Long = runBlocking {
        sendNotification(newNotification())
        saveArgumentAsync(argumentJsonRaw).await()

    }

    private fun saveArgumentAsync(argumentJsonRaw: ArgumentJsonRaw) = async {
        return@async apiFactory.insertArgumentRaw(argumentJsonRaw)
    }

    private fun saveArgumentWithoutAnswer(argumentJsonRaw: ArgumentJsonRaw): Long = runBlocking {
        sendNotification(newNotification())
        saveArgumentWithoutAnswerAsync(argumentJsonRaw).await()
    }

    private fun saveArgumentWithoutAnswerAsync(argumentJsonRaw: ArgumentJsonRaw) = async {
        return@async apiFactory.insertArgumentWithoutAnswerRaw(argumentJsonRaw)
    }

    private fun newNotification(): PushNotification {
        val notificationData = NotificationData("argument", "new Argument")
        return PushNotification(notificationData, topic)
    }

    private fun sendNotification(notification: PushNotification) {
        launch {
            val response = notificationObject.postNotification(notification)
            if (response.isSuccessful) {
                Log.e("notification", "notification successfully sent")
            } else {
                val jsonObject = JSONObject(response.errorBody()!!.toString())
                val st = jsonObject.getString("message")
                Log.e("notification Error", st)
            }
        }
    }
}