package com.gedar0082.debater.viewmodel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gedar0082.debater.R
import com.gedar0082.debater.model.net.api.ApiFactory
import com.gedar0082.debater.model.net.api.NotificationObject
import com.gedar0082.debater.model.net.notification.NotificationData
import com.gedar0082.debater.model.net.notification.PushNotification
import com.gedar0082.debater.model.net.pojo.*
import com.gedar0082.debater.util.ArgumentList
import com.gedar0082.debater.util.CurrentUser
import com.gedar0082.debater.util.InterScreenController
import com.gedar0082.debater.util.Util
import com.gedar0082.debater.view.adapters.ThesisOptionsAdapter
import kotlinx.coroutines.*
import org.json.JSONObject
import org.w3c.dom.Text
import kotlin.coroutines.CoroutineContext


class ArgumentMapViewModel : ViewModel(), CoroutineScope {

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
    lateinit var navController: NavController
    lateinit var topic: String
    var ruleType = 0
    var arguments = MutableLiveData<List<ArgumentJson>>()
    var debateId: Long = 0
    var thesisId: Long = 0
    var type = "issue"
    lateinit var creator: PersonJson
    lateinit var debateWithPersons : List<DebateWithPersons>

    val exceptionLiveData = MutableLiveData<String>()

    override val coroutineContext: CoroutineContext
        get() = Job()

    private val apiFactory = ApiFactory.service
    private val notificationObject = NotificationObject.service

    fun toThesisMapFragment(){
        navController.popBackStack()
    }

    fun getPersonDebate(id : Long) {
        try {
            val a = getDebateWithPersons(id)
            val b = getListOfUniqueDebatesWithPersons(a)
            debateWithPersons = b
        }catch (e: Exception){
            e.printStackTrace()
            exceptionLiveData.postValue("exception")
        }


    }

    fun argumentOptions(){
        if (ruleType == 3){
            getPersonDebate(debateId)
            val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
            val li = LayoutInflater.from(context)
            val promptView: View = li.inflate(R.layout.thesis_options, null)
            confirm.setView(promptView)

            val creatorNameView: TextView = promptView.findViewById(R.id.creator_name)
            val creatorEmailView: TextView = promptView.findViewById(R.id.creator_email)
            val recyclerView : RecyclerView = promptView.findViewById(R.id.participants_recycler)
            val myAdapter = ThesisOptionsAdapter(debateWithPersons.first().personsWithRights){
                changeRights(it)
            }
            val myLayoutManager = LinearLayoutManager(context)
            val itemDecoration = DividerItemDecoration(context, myLayoutManager.orientation)
            recyclerView.apply {
                setHasFixedSize(true)
                adapter = myAdapter
                layoutManager = myLayoutManager
                addItemDecoration(itemDecoration)
            }

            val exit = promptView.findViewById<Button>(R.id.thesis_options_exit)

            creatorEmailView.text = debateWithPersons.first().findCreator()?.email ?: "do not exist"
            creatorNameView.text = debateWithPersons.first().findCreator()?.nickname ?: "do not exist"
            confirm.setCancelable(true)
            confirm.create().apply {
                setOnShowListener { dialog ->
                    exit.setOnClickListener {
                        discussionExitApprove(dialog)

                    }
                }
                show()
            }
        }
    }

    fun createNewArgument(argument: ArgumentJson?) {
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.dialog_new_argument, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)

        val title = promptView.findViewById<EditText>(R.id.argument_title_input)
        val statement = promptView.findViewById<EditText>(R.id.argument_statement_input)
        val response = promptView.findViewById<TextView>(R.id.argument_response)

        response.text = "${argument?.title ?: ""}\n${argument?.statement ?: ""}"

        val spinner = promptView.findViewById<Spinner>(R.id.argument_spinner)
        spinner.adapter = getSpinnerAdapter()
        spinner.onItemSelectedListener = getSpinnerOnItemSelectedListener()
        if (ruleType == 1) spinner.visibility = View.INVISIBLE

        confirm.setPositiveButton("Create") { dialog, _ ->
            run {
                if (InterScreenController.chooseAnswerArg == 1) {
                    ArgumentList.argumentList.add(
                        ArgumentJsonRaw(
                            0,
                            title.text.toString(),
                            statement.text.toString(),
                            argument!!.id,
                            debateId,
                            thesisId,
                            CurrentUser.id,
                            Util.getCurrentDate(),
                            InterScreenController.thesisPressed?.type ?: 1
                        )
                    )
                    dialog.cancel()
                    InterScreenController.chooseAnswerArg = 2
                    navController.popBackStack()
                } else {
                    val newArgument = ArgumentJsonRaw(
                        0,
                        title.text.toString(),
                        statement.text.toString(),
                        if (argument!!.id == Long.MAX_VALUE) 0 else argument.id,
                        debateId,
                        null,
                        CurrentUser.id,
                        Util.getCurrentDate(),
                        getIntType()
                    )
                    if (argument.id == 0L){
                        saveArgument(newArgument)
                    }
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
        val titleText = promptView.findViewById<TextView>(R.id.arg_title)
        val statementText = promptView.findViewById<TextView>(R.id.arg_statement)
        titleText.text = argument.title
        statementText.text = argument.statement
        confirm.create()
        confirm.show()
    }

    fun getArguments(id: Long) {
        launch {
            runCatching { apiFactory.getArgumentsByDebateId(id) }.onSuccess {
                arguments.postValue(it)

            }.onFailure { it.printStackTrace() }
        }
    }

    private fun getSpinnerAdapter(): ArrayAdapter<String> {
        val spinnerList = listOf("issue", "confirmation", "refutation", "position" )
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
                type = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getIntType(): Int{
        return when(type){
            "issue" -> 1
            "confirmation" -> 2
            "refutation" -> 3
            "position" -> 4
            else -> 1
        }
    }

    private fun saveArgument(argumentJsonRaw: ArgumentJsonRaw): Long = runBlocking {
        sendNotification(newNotification())
        saveArgumentAsync(argumentJsonRaw).await()

    }

    private fun saveArgumentAsync(argumentJsonRaw: ArgumentJsonRaw) = async {
        return@async apiFactory.insertArgumentRaw(argumentJsonRaw)
    }

    private fun newNotification(): PushNotification {
        val notificationData = NotificationData("argument", "new Argument")
        return PushNotification(notificationData, topic)
    }

    private fun sendNotification(notification: PushNotification) {
        Log.e("fcm", "topic is ${notification.to}")
        Log.e("fcm", "topic is ${topic}")
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

    private fun getDebateWithPersons(id: Long) : List<PersonDebateJson> = runBlocking {
        getDebateWithPersonsAsync(id).await()
    }

    private fun getDebateWithPersonsAsync(id: Long) = async {
        return@async apiFactory.getPersonDebateByDebateId(id)
    }

    private fun deletePersonDebate(debateId: Long, rightsIdOld: Long, rightsIdNew: Long, personId: Long){
        launch {
            runCatching {
                apiFactory.deletePersonDebate(PersonDebateRawJson(debateId, personId,rightsIdOld))
            }.onSuccess {
                println("successful deletePersonDebate")
                savePersonDebate(debateId, rightsIdNew, personId)
            }.onFailure {
                it.printStackTrace()
                exceptionLiveData.postValue("exception")
            }
        }
    }

    private fun deletePersonDebate(debateId: Long, rightsIdOld: Long, personId: Long){
        launch {
            runCatching {
                apiFactory.deletePersonDebate(PersonDebateRawJson(debateId, personId,rightsIdOld))
            }.onSuccess {
                println("successful deletePersonDebate")
            }.onFailure {
                it.printStackTrace()
                exceptionLiveData.postValue("exception")
            }
        }
    }

    private fun savePersonDebate(debateId: Long, rightsId: Long, personId: Long) {
        launch {
            runCatching {
                val personDebateRawJson = PersonDebateRawJson(debateId, personId, rightsId)
                apiFactory.insertRawPersonDebate(personDebateRawJson)
            }.onSuccess {
                println("successful savePersonDebate")
            }.onFailure {
                it.printStackTrace()
                exceptionLiveData.postValue("exception")
            }
        }
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

    private fun findPersonsOfDebate(list: List<PersonDebateJson>, debate: DebateJson)
            : List<PersonRightsJson> {
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

    private fun discussionExitApprove(dialogInterface: DialogInterface){
        val alertDialog = AlertDialog.Builder(context, R.style.myDialogStyle)
        alertDialog.setTitle("Exit from discussion")
            .setMessage("Are you sure?")
            .setPositiveButton("Yse"){ approveDialog, _ ->
                if (debateWithPersons.first().findCreator()!!.id != CurrentUser.id) {
                    deletePersonDebate(debateId,  getPersonRights(debateWithPersons.first().personsWithRights), CurrentUser.id)
                }
                Toast.makeText(context, "Successfully exit", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_thesisMapFragment_to_debateFragment)
                dialogInterface.cancel()
                approveDialog.cancel()
            }
            .setNegativeButton("No"){ approveDialog, _ ->
                approveDialog.cancel()
                dialogInterface.cancel()
            }
            .create()
            .show()
    }

    private fun changeRights(personRightsJson: PersonRightsJson){
        if (CurrentUser.id != debateWithPersons.first().findCreator()!!.id){
            Toast.makeText(context, "You don't allow to change users' rights", Toast.LENGTH_SHORT).show()
        }else if(CurrentUser.id == personRightsJson.person.id){
            Toast.makeText(context, "You can not change your rights", Toast.LENGTH_SHORT).show()
        }else{
            val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
            val li = LayoutInflater.from(context)
            val promptView: View = li.inflate(R.layout.thesis_change_rights, null)
            confirm.setView(promptView)

            val writeCheckBox: CheckBox = promptView.findViewById(R.id.thesis_change_rights_write_checkbox)
            if (personRightsJson.rights.write == 1){
                writeCheckBox.toggle()
            }

            val okBtn = promptView.findViewById<Button>(R.id.thesis_change_rights_ok)
            val noBtn = promptView.findViewById<Button>(R.id.thesis_change_rights_no)

            confirm.setCancelable(false)
            confirm.create().apply {
                setOnShowListener { dialog ->
                    okBtn.setOnClickListener {
                        if (writeCheckBox.isChecked){
                            updateRights(debateId, personRightsJson.rights.id, 4, personRightsJson.person.id)
                        }else{
                            updateRights(debateId, personRightsJson.rights.id, 2, personRightsJson.person.id)
                        }
                        dialog.cancel()
                    }
                    noBtn.setOnClickListener {
                        dialog.cancel()
                    }
                }
                show()
            }
        }

    }

    private fun getPersonRights(personRights: List<PersonRightsJson>): Long{
        personRights.forEach { if (it.person.id == CurrentUser.id) return it.rights.id }
        return 1
    }

    private fun updateRights(debateId: Long, rightsIdOld: Long, rightsIdNew: Long, personId: Long){
        println("updating rights")
        deletePersonDebate(debateId, rightsIdOld, rightsIdNew, personId)
    }
}