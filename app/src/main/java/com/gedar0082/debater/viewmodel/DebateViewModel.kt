package com.gedar0082.debater.viewmodel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gedar0082.debater.R
import com.gedar0082.debater.model.net.api.ApiFactory
import com.gedar0082.debater.model.net.api.NotificationObject
import com.gedar0082.debater.model.net.notification.NotificationData
import com.gedar0082.debater.model.net.notification.PushNotification
import com.gedar0082.debater.model.net.pojo.*
import com.gedar0082.debater.util.CurrentUser
import kotlinx.coroutines.*
import okhttp3.WebSocket
import org.json.JSONObject
import java.sql.SQLOutput
import java.sql.Timestamp
import java.text.SimpleDateFormat
import kotlin.coroutines.CoroutineContext


const val TOPIC = "/topics/myTopic"

open class DebateViewModel : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job()




    val debates = MutableLiveData<List<DebateJson>>()
    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
    private val apiFactory = ApiFactory.service
    private val notificationObject = NotificationObject.service
    var rule = "classic"

    fun getDebates(){
        launch {
            runCatching { apiFactory.getAllDebates()}.onSuccess {
                print("finding detates = $it")
                debates.postValue(it)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun saveDebate(debateJson: DebateJson) : Long {
        launch {
            runCatching { val debateJson = apiFactory.insertDebate(debateJson) }.onSuccess {
                println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
                return@onSuccess it
            }.onFailure {
                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                it.printStackTrace()
            }
        }
        return 0L
    }

    private fun saveDebateA(debateJson: DebateJson) : DebateJson = runBlocking {
        saveDebateAsync(debateJson).await()
    }

    private fun saveDebateAsync(debateJson: DebateJson) = async {
        return@async apiFactory.insertDebate(debateJson)
    }

    private fun savePersonDebate(id: Long){
        launch {
            runCatching {
                val personDebateRawJson = PersonDebateRawJson(id, CurrentUser.id, 1)
            }.onSuccess {
                println("################################################3")
                println("save personDebate")
                println(it)

            }.onFailure {
                println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
                it.printStackTrace()
            }
        }
    }

    private fun newNotification(): PushNotification{
        println("NOTIFICATION")
        val notificationData = NotificationData("sosi", "pisku")
        return PushNotification(notificationData, TOPIC)
    }

    private fun createPersonDebate(debateJson: DebateJson) : PersonDebateJson{
        val rightJson = RightJson(1, 1, 1, 1,1 )
        val personJson = PersonJson(
            nickname = CurrentUser.nickname,
            id = CurrentUser.id,
            email = CurrentUser.email,
            password = CurrentUser.password
        )
        val personDebateJson = PersonDebateJson(
            debateJson,
            personJson,
            rightJson
        )
        println(personDebateJson)
        return personDebateJson
    }

    fun createNewDebate(){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.dialog_new_debate, null)
        confirm.setView(promptView)
        val spinner = promptView.findViewById<Spinner>(R.id.debate_rule)
        spinner.adapter = getSpinnerAdapter()
        spinner.onItemSelectedListener = getSpinnerOnItemSelectedListener()
        confirm.setCancelable(true)
            .setPositiveButton("Create") { dialog, _ ->
                run {
                    val debateJson = DebateJson(0,
                        promptView.findViewById<EditText>(R.id.debate_name).text.toString(),
                        promptView.findViewById<EditText>(R.id.debate_description).text.toString(),
                        getCurrentDate(),
                        getRegulationsJsonFromRule(rule))
                    println("save debate $debateJson")
                    val id = saveDebateA(debateJson)
                    savePersonDebate(id.id)
                    sendNotification(newNotification())
                    dialog.cancel()
                }
            }
        confirm.create()
        confirm.show()
    }

    private fun getSpinnerAdapter() : ArrayAdapter<String>{
        val spinnerList = listOf("classic", "british", "free")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    private fun getSpinnerOnItemSelectedListener() : AdapterView.OnItemSelectedListener{
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                rule = parent.getItemAtPosition(position) as String
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate() : Timestamp{
        val pattern = "yyyy-MM-dd HH:mm:ss.SSSSSSSSS"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return Timestamp.valueOf(simpleDateFormat.format(System.currentTimeMillis()))
    }

    private fun getRegulationsJsonFromRule(stringRule : String) : RegulationsJson{
        val hasReferee = 0
        val ruleType : Int = when(stringRule){
            "classic" -> 1
            "british" -> 2
            "free" -> 3
            else -> 3
        }
        return RegulationsJson(0, ruleType, hasReferee)
    }

    private fun sendNotification(notification: PushNotification){
        launch {
            val response = notificationObject.postNotification(notification)
            if (response.isSuccessful){
                Log.e("notification", "notification successfully sent")
            }else{
                val jsonObject : JSONObject = JSONObject(response.errorBody()!!.string())
                val st = jsonObject.getString("message")
                Log.e("notification", "error in notification")
                Log.e("notification Error", "${response.errorBody()}")
                Log.e("notification Error", "$st")
                Log.e("notification Error", "${response.code()}")
            }
        }
    }

}