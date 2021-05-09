package com.gedar0082.debater.viewmodel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.gedar0082.debater.R
import com.gedar0082.debater.model.net.api.ApiFactory
import com.gedar0082.debater.model.net.api.NotificationObject
import com.gedar0082.debater.model.net.notification.NotificationData
import com.gedar0082.debater.model.net.notification.PushNotification
import com.gedar0082.debater.model.net.pojo.*
import com.gedar0082.debater.util.CurrentUser
import kotlinx.coroutines.*
import org.json.JSONObject
import org.w3c.dom.Text
import java.sql.Timestamp
import java.text.SimpleDateFormat
import kotlin.coroutines.CoroutineContext


const val TOPIC = "/topics/maindebate"

class DebateViewModel : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job()

    val debates = MutableLiveData<List<DebateJson>>()
    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
    lateinit var navController: NavController
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
                    val id = saveDebate(debateJson)
                    savePersonDebate(id.id, 6)
                    sendNotification(newNotification())
                    dialog.cancel()
                }
            }
        confirm.create()
        confirm.show()
    }

    fun openDebate(debateJson: DebateJson){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.debate_open, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)
        val debateOpenName = promptView.findViewById<TextView>(R.id.debate_open_name)
        val debateOpenDescription = promptView.findViewById<TextView>(R.id.debate_open_description)
        val debateOpenBtn = promptView.findViewById<Button>(R.id.debate_open_btn_enter)
        debateOpenName.text = debateJson.name
        debateOpenDescription.text = debateJson.description
        confirm.create().apply {
            setOnShowListener { dialog ->
                debateOpenBtn.setOnClickListener {
                    savePersonDebate(debateJson.id, 4)  //пока что на правила не реагирует. Решить возможно с помощью какого-то контейнера.
                    navigate(debateJson)
                    dialog.cancel()
                }
            }
            show()
        }

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
                val jsonObject = JSONObject(response.errorBody()!!.string())
                val st = jsonObject.getString("message")
                Log.e("notification", "error in notification")
                Log.e("notification Error", "${response.errorBody()}")
                Log.e("notification Error", "${response.code()}")
            }
        }
    }

    private fun saveDebate(debateJson: DebateJson) : DebateJson = runBlocking {
        saveDebateAsync(debateJson).await()
    }

    private fun saveDebateAsync(debateJson: DebateJson) = async {
        return@async apiFactory.insertDebate(debateJson)
    }

    private fun savePersonDebate(debateId: Long, rightsId: Long){
        launch {
            runCatching {
                val personDebateRawJson = PersonDebateRawJson(debateId, CurrentUser.id, rightsId)
                apiFactory.insertRawPersonDebate(personDebateRawJson)
            }.onSuccess {
                println(it)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun newNotification(): PushNotification{
        println("NOTIFICATION")
        val notificationData = NotificationData("debate", "pisku")
        return PushNotification(notificationData, TOPIC)
    }

    private fun navigate(debateJson: DebateJson){
        val bundle: Bundle = bundleOf(Pair("id", debateJson.id), Pair("name", debateJson.name))
        navController.navigate(R.id.action_debateFragment_to_thesisMapFragment, bundle)
    }

}