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
import java.sql.Timestamp
import java.text.SimpleDateFormat
import kotlin.coroutines.CoroutineContext


const val TOPIC = "/topics/main_debate"

class DebateViewModel : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job()

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
    lateinit var navController: NavController
    private val apiFactory = ApiFactory.service
    private val notificationObject = NotificationObject.service
    var rule = "classic"

    val debateWithPersons = MutableLiveData<List<DebateWithPersons>>()
    val exceptionLiveData = MutableLiveData<String>()


    fun getPersonDebate() {
        launch {
            runCatching { apiFactory.getPersonDebate() }.onSuccess {
                debateWithPersons.postValue(getListOfUniqueDebatesWithPersons(it))
            }.onFailure {
                it.printStackTrace()
                exceptionLiveData.postValue("exception")
            }
        }
    }

    fun createNewDebate() {
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
                    val debateJson = DebateJson(
                        0,
                        stringCutter(promptView.findViewById<EditText>(R.id.debate_name).text.toString()),
                        stringCutter(promptView.findViewById<EditText>(R.id.debate_description).text.toString()),
                        getCurrentDate(),
                        getRegulationsJsonFromRule(rule)
                    )
                    try {
                        val id = saveDebate(debateJson)
                        savePersonDebate(id.id, 6)
                        sendNotification(newNotification())
                    }catch (e: Exception){
                        e.printStackTrace()
                        exceptionLiveData.postValue("exception")
                    }

                    dialog.cancel()
                }
            }
        confirm.create()
        confirm.show()
    }

    private fun stringCutter(target: String): String{
        return if(target.length > 254) target.substring(0, 253)
        else target
    }

    fun openDebate(debateJson: DebateWithPersons) {
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.debate_open, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)
        val debateOpenName = promptView.findViewById<TextView>(R.id.debate_open_name)
        val debateOpenDescription = promptView.findViewById<TextView>(R.id.debate_open_description)
        val debateOpenRules = promptView.findViewById<TextView>(R.id.debate_open_rules)
        val debateOpenBtn = promptView.findViewById<Button>(R.id.debate_open_btn_enter)
        debateOpenName.text = debateJson.debate.name
        debateOpenDescription.text = debateJson.debate.description
        debateOpenRules.text = getStringRules(debateJson.debate.regulations.ruleType)
        confirm.create().apply {
            setOnShowListener { dialog ->
                debateOpenBtn.setOnClickListener {
                    if(!isPersonInDebate(debateJson)){
                        savePersonDebate(
                            debateJson.debate.id,
                            4
                        )
                    }
                    navigate(debateJson)
                    dialog.cancel()
                }
            }
            show()
        }

    }

    private fun getStringRules(rule : Int): String{
        when(rule){
            1 -> return "classic"
            3 -> return "free"
        }
        return "free"
    }

    private fun isPersonInDebate(debateWithPersons: DebateWithPersons) : Boolean{
        debateWithPersons.personsWithRights.forEach {
            if (it.person.id == CurrentUser.id) return true
        }
        return false
    }

    private fun getSpinnerAdapter(): ArrayAdapter<String> {
        val spinnerList = listOf("classic", "free")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    private fun getSpinnerOnItemSelectedListener(): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                rule = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): Timestamp {
        val pattern = "yyyy-MM-dd HH:mm:ss.SSS"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return Timestamp.valueOf(simpleDateFormat.format(System.currentTimeMillis())) // кажется вставляет локальное время, а не GMT. Надо будет проверить.
    }

    private fun getRegulationsJsonFromRule(stringRule: String): RegulationsJson {
        val ruleType: Int = when (stringRule) {
            "classic" -> 1
            "british" -> 2
            "free" -> 3
            else -> 3
        }
        return RegulationsJson(0, ruleType)
    }

    private fun sendNotification(notification: PushNotification) {
        launch {
            val response = notificationObject.postNotification(notification)
            if (response.isSuccessful) {
                Log.e("notification", "notification successfully sent")
            } else {
                val jsonObject = JSONObject(response.errorBody()!!.toString())
                val st = jsonObject.getString("message")

                Log.e("notification", "error in notification")
                Log.e("notification message", st)
                Log.e("notification Error", "${response.errorBody()}")
                Log.e("notification Error", "${response.code()}")
            }
        }
    }

    private fun saveDebate(debateJson: DebateJson): DebateJson = runBlocking {
        saveDebateAsync(debateJson).await()
    }

    private fun saveDebateAsync(debateJson: DebateJson) = async {
        return@async apiFactory.insertDebate(debateJson)
    }

    private fun savePersonDebate(debateId: Long, rightsId: Long) {
        launch {
            runCatching {
                val personDebateRawJson = PersonDebateRawJson(debateId, CurrentUser.id, rightsId)
                apiFactory.insertRawPersonDebate(personDebateRawJson)
            }.onSuccess {
                println("save personDebate success")
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun newNotification(): PushNotification {
        val notificationData = NotificationData("debate", "new debate")
        return PushNotification(notificationData, TOPIC)
    }

    private fun navigate(debateJson: DebateWithPersons) {
        val bundle: Bundle =
            bundleOf(Pair("id", debateJson.debate.id), Pair("name", debateJson.debate.name))
        navController.navigate(R.id.action_debateFragment_to_thesisMapFragment, bundle)
    }

    private fun getListOfUniqueDebatesWithPersons(list: List<PersonDebateJson>): List<DebateWithPersons> {
        val resultList = mutableListOf<DebateWithPersons>()
        val listOfUniqueDebates = mutableListOf<DebateJson>()
        list.forEach { if (!listOfUniqueDebates.contains(it.debate)) listOfUniqueDebates.add(it.debate) }
        listOfUniqueDebates.forEach {
            val personRightsJsonList = findPersonsOfDebate(list, it)
            resultList.add(DebateWithPersons(it, personRightsJsonList))
        }
        return resultList
    }

    private fun findPersonsOfDebate(
        list: List<PersonDebateJson>,
        debate: DebateJson
    ): List<PersonRightsJson> {
        val personsRights = mutableListOf<PersonRightsJson>()
        list.forEach {
            if (it.debate == debate) personsRights.add(
                PersonRightsJson(
                    it.person,
                    it.rights
                )
            )
        }
        return personsRights
    }

}