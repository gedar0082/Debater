package com.gedar0082.debater.viewmodel


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
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
import com.gedar0082.debater.util.*
import com.gedar0082.debater.view.adapters.ThesisOptionsAdapter
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext


class ThesisMapViewModel : ViewModel(), CoroutineScope {

    @SuppressLint("StaticFieldLeak")
    lateinit var context: Context
    lateinit var debateWithPersons : List<DebateWithPersons>
    lateinit var topic : String
    lateinit var res: Resources
    var theses = MutableLiveData<List<ThesisJson>>()
    var debateId: Long = 0
    private var tId: Long = 0

    var rule = 0

    val exceptionLiveData = MutableLiveData<String>()

    override val coroutineContext: CoroutineContext
        get() = Job()

    lateinit var navController: NavController

    private val thesisTitleLive = MutableLiveData<String>()
    private val thesisShortLive = MutableLiveData<String>()
    private val thesisStatementLive = MutableLiveData<String>()

    private val apiFactory = ApiFactory.service
    private val notificationObject = NotificationObject.service

    fun getTheses(id: Long){
        launch {
            kotlin.runCatching { apiFactory.getThesesByDebateId(id) }.onSuccess {
                tId = id
                theses.postValue(it)
            }.onFailure {
                it.printStackTrace()
                exceptionLiveData.postValue("exception")
            }
        }
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

    fun thesisOptions(){
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

    private fun getPersonRights(personRights: List<PersonRightsJson>): Long{
        personRights.forEach { if (it.person.id == CurrentUser.id) return it.rights.id }
        return 1
    }

    fun createNewThesis(thesis: ThesisJson, navController: NavController) {
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.dialog_new_thesis, null)
        val linearLayout = promptView.findViewById<LinearLayout>(R.id.dialog_new_thesis_linear)
        confirm.setView(promptView)

        val thesisTitleInput: EditText = promptView.findViewById(R.id.thesis_title_input)
        val thesisShortInput: EditText = promptView.findViewById(R.id.thesis_short_input)
        val thesisStatementInput: EditText = promptView.findViewById(R.id.thesis_statement_input)

        val thesisResponse : TextView = promptView.findViewById(R.id.thesis_response)
        thesisResponse.text = thesis.title

        thesisResponse.setOnClickListener {
            if (thesis.person != null) openThesis(thesis, navController)
            else Toast.makeText(context, "Nothing to show", Toast.LENGTH_SHORT).show()
        }

        if (InterScreenController.chooseAnswerArg == 0){
            ArgumentList.argumentList.clear()
        }

        if (InterScreenController.chooseAnswerArg == 3){
            InterScreenController.argumentPressed?.let {

                thesisTitleInput.setText(thesisTitleLive.value ?: "")
                thesisShortInput.setText(thesisShortLive.value ?: "")
                thesisStatementInput.setText(thesisStatementLive.value ?: "")

                ArgumentList.argumentList.forEach {
                    val answerTextView = TextView(context)
                    answerTextView.textSize = 20F
                    answerTextView.setTextColor(ResourcesCompat.getColor(res, R.color.grey, null))
                    answerTextView.text = String.format(res.getString(R.string.argument_in_thesis_text),
                            it.title + "\n",
                            it.statement + "\n"
                    )
                    linearLayout.addView(answerTextView)
                }

            }
            InterScreenController.chooseAnswerArg =0
        }

        val argBtn1 = promptView.findViewById<Button>(R.id.btn_answer1)

        confirm.setCancelable(false)
            .setPositiveButton("Create") { dialog, _ ->
                run {

                    if (getRoundNumber() == 0){
                        Toast.makeText(context, "Debate is over", Toast.LENGTH_SHORT).show()
                        dialog.cancel()
                    }else{
                        val newThesisRaw = ThesisJsonRaw(0,
                            stringCutter1024(thesisTitleInput.text.toString()),
                            stringCutter1024(thesisShortInput.text.toString()),
                            stringCutter1024(thesisStatementInput.text.toString()),
                            getRoundNumber(),
                            thesis.id,
                            debateId,
                            CurrentUser.id,
                            Util.getCurrentDate(),
                            getAnswerType(thesis))

                        val currentThesisId = if(thesis.id == 0L){
                            saveThesis(newThesisRaw)
                        } else saveThesis(newThesisRaw)
                        if (ArgumentList.argumentList.isNotEmpty()){
                            ArgumentList.argumentList.forEach {
                                if (it.answer_id == null || it.answer_id == 0L || it.answer_id == Long.MAX_VALUE)
                                    saveArgument(
                                        ArgumentJsonRaw(
                                        0,
                                        it.title,
                                        it.statement,
                                        0,
                                        it.debate_id,
                                        currentThesisId,
                                        CurrentUser.id,
                                        it.date_time,
                                        getAnswerType(thesis))
                                    )
                                else saveArgument(ArgumentJsonRaw(
                                    0,
                                    it.title,
                                    it.statement,
                                    it.answer_id,
                                    it.debate_id,
                                    currentThesisId,
                                    CurrentUser.id,
                                    it.date_time,
                                    getAnswerType(thesis))
                                )
                            }
                        }
                        getTheses(debateId)
                        sendNotification(newNotification())
                        dialog.cancel()
                    }


                }
            }
        confirm.setNegativeButton("Dismiss"){ dialog, _ ->
            dialog.cancel()
        }
        confirm.create().apply {
            setOnShowListener { dialog ->
                argBtn1.setOnClickListener{

                    InterScreenController.chooseAnswerArg = 1
                    InterScreenController.thesisPressed = thesis

                    thesisTitleLive.postValue(thesisTitleInput.text.toString())
                    thesisShortLive.postValue(thesisShortInput.text.toString())
                    thesisStatementLive.postValue(thesisStatementInput.text.toString())
                    val bundle = bundleOf(Pair("debate_id", debateId), Pair("ruleType", rule))
                    navController.saveState()
                    navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)
                    dialog.cancel()
                }
            }
            show()
        }
    }

    private fun getAnswerType(thesis: ThesisJson) : Int =
        if (thesis.type == 1 || thesis.type == 3) 2 else 3



    fun openThesis(thesis: ThesisJson, navController: NavController){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.thesis_open, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)

        val textTitle = promptView.findViewById<TextView>(R.id.thesis_open_title)
        val textShort = promptView.findViewById<TextView>(R.id.thesis_open_short)
        val textStatement = promptView.findViewById<TextView>(R.id.thesis_open_statement)



        textTitle.text = thesis.title
        textShort.text = thesis.shrt
        textStatement.text = thesis.statement

        confirm.create().apply {
            setOnShowListener { dialog ->
                val btn = promptView.findViewById<Button>(R.id.btn_answer)
                btn.setOnClickListener {
                    createNewThesis(thesis, navController)
                    dialog.cancel()
                }
                promptView.setOnTouchListener(getOnSwipeTouchListener(context, navController, thesis.id, dialog))
            }
            show()
        }
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

    private fun updateRights(debateId: Long, rightsIdOld: Long, rightsIdNew: Long, personId: Long){
        println("updating rights")
        deletePersonDebate(debateId, rightsIdOld, rightsIdNew, personId)
    }

    fun checkRights(): Boolean = getWriteId() == 1

    private fun getWriteId(): Int{
        val list = debateWithPersons.first().personsWithRights
        list.forEach {
            if (it.person.id == CurrentUser.id){
                return it.rights.write
            }

        }
        return 0
    }

    private fun getRoundNumber(): Int{
        return when (theses.value!!.size) {
            in 0..1 -> 1
            in 2..3 -> 2
            in 4..5 -> 3
            in 6..7 -> 4
            else -> 0
        }
    }

    private fun getOnSwipeTouchListener(context: Context, navController: NavController, thesisId: Long, listener: DialogInterface)
    = object : OnSwipeTouchListener(context) {
        override fun onSwipeLeft() {
            val bundle = bundleOf(Pair("debate_id", debateId), Pair("thesis_id", thesisId), Pair("ruleType", rule))
            navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)
            listener.cancel()
        }
    }

    private fun newNotification(): PushNotification{
        val notificationData = NotificationData("thesis", "New Thesis")
        return PushNotification(notificationData, topic)
    }

    private fun sendNotification(notification: PushNotification){
        launch {
            val response = notificationObject.postNotification(notification)
            if (response.isSuccessful){
                Log.e("notification", "notification successfully sent")
            }else{
                val jsonObject = JSONObject(response.errorBody()!!.toString())
                val st = jsonObject.getString("message")
                Log.e("notification Error", st)
            }
        }
    }

    private fun stringCutter1024(target: String): String{
        return if(target.length > 1024) target.substring(0, 1024)
        else target
    }

    private fun getListOfUniqueDebatesWithPersons(list: List<PersonDebateJson>): List<DebateWithPersons> {
        val resultList = mutableListOf<DebateWithPersons>()
        val listOfUniqueDebates = mutableListOf<DebateJson>()
        list.forEach { if (!listOfUniqueDebates.contains(it.debate)) listOfUniqueDebates.add(it.debate) }

        listOfUniqueDebates.forEach {
            val personRightsJsonList = findPersonsOfDebate(list, it)
            resultList.add(DebateWithPersons(it, personRightsJsonList))
        }
        println(resultList)
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

    private fun saveThesis(thesis: ThesisJsonRaw) : Long = runBlocking{
        saveThesisAsync(thesis).await()
    }

    private fun saveThesisAsync(thesis: ThesisJsonRaw) = async{
        return@async apiFactory.insertThesisRaw(thesis)
    }

    private fun getDebateWithPersons(id: Long) : List<PersonDebateJson> = runBlocking {
        getDebateWithPersonsAsync(id).await()
    }

    private fun getDebateWithPersonsAsync(id: Long) = async {
        return@async apiFactory.getPersonDebateByDebateId(id)
    }

    private fun saveArgument(argumentJsonRaw: ArgumentJsonRaw): Long = runBlocking{
        saveArgumentAsync(argumentJsonRaw).await()
    }

    private fun saveArgumentAsync(argumentJsonRaw: ArgumentJsonRaw) = async {
        return@async apiFactory.insertArgumentRaw(argumentJsonRaw)
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

}