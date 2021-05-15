package com.gedar0082.debater.viewmodel


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
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

    override val coroutineContext: CoroutineContext
        get() = Job()

    private val thesisIntroLive = MutableLiveData<String>()
    private val thesisDefinitionLive = MutableLiveData<String>()
    private val thesisProblemLive = MutableLiveData<String>()
    private val thesisPlanLive = MutableLiveData<String>()
    private val thesisCaseIntroLive = MutableLiveData<String>()
    private val thesisCaseDescLive = MutableLiveData<String>()

    private val apiFactory = ApiFactory.service
    private val notificationObject = NotificationObject.service

    fun getTheses(id: Long){
        launch {
            kotlin.runCatching { apiFactory.getThesesByDebateId(id) }.onSuccess {
                theses.postValue(it)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun getPersonDebate(id : Long) {
        val a = getDebateWithPersons(id)
        Log.e("async", "getDebateWithPersons $a")
        val b = getListOfUniqueDebatesWithPersons(a)
        Log.e("async", "getUnique $b")
        debateWithPersons = b
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
        println("list lol : ${debateWithPersons.first().personsWithRights}")
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
        creatorEmailView.text = debateWithPersons.first().findCreator()!!.email
        creatorNameView.text = debateWithPersons.first().findCreator()!!.nickname
        confirm.setCancelable(true)
        confirm.create().show()

    }

    fun createNewThesis(thesis: ThesisJson, navController: NavController) {
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.dialog_new_thesis, null)
        confirm.setView(promptView)

        val thesisIntroInput: EditText = promptView.findViewById(R.id.thesis_intro_input)
        val thesisDefinitionInput: EditText = promptView.findViewById(R.id.thesis_definition_input)
        val thesisProblemInput: EditText = promptView.findViewById(R.id.thesis_problem_input)
        val thesisPlanInput: EditText = promptView.findViewById(R.id.thesis_plan_input)
        val thesisCaseIntroInput: EditText = promptView.findViewById(R.id.thesis_case_intro_input)
        val thesisCaseDescInput: EditText = promptView.findViewById(R.id.thesis_case_description_input)

        val thesisResponse : TextView = promptView.findViewById(R.id.thesis_response)

        val answerArg1: TextView = promptView.findViewById(R.id.text_answer_for1)
        val answerArg2: TextView = promptView.findViewById(R.id.text_answer_for2)
        val answerArg3: TextView = promptView.findViewById(R.id.text_answer_for3)

        thesisResponse.text = thesis.intro

        thesisResponse.setOnClickListener {
            if (thesis.person != null) openThesis(thesis, navController)
            else Toast.makeText(context, "Nothing to show", Toast.LENGTH_SHORT).show()
        }

        if (InterScreenController.chooseAnswerArg == 0){
            ArgumentList.argumentList.clear()
        }

        if (InterScreenController.chooseAnswerArg == 3){
            InterScreenController.argumentPressed?.let {

                thesisIntroInput.setText(thesisIntroLive.value ?: "")
                thesisDefinitionInput.setText(thesisDefinitionLive.value ?: "")
                thesisProblemInput.setText(thesisProblemLive.value ?: "")
                thesisPlanInput.setText(thesisPlanLive.value ?: "")
                thesisCaseIntroInput.setText(thesisCaseIntroLive.value ?: "")
                thesisCaseDescInput.setText(thesisCaseDescLive.value ?: "")

                when (ArgumentList.argumentList.size) {
                    0 -> {
                        answerArg1.text = ""
                        answerArg2.text = ""
                        answerArg3.text = ""
                    }
                    1 -> {
                        answerArg1.text = String.format(res.getString(R.string.argument_in_thesis_text),
                            ArgumentList.argumentList[0].statement,
                            ArgumentList.argumentList[0].clarification,
                            ArgumentList.argumentList[0].evidence,
                            ArgumentList.argumentList[0].summary)
                        answerArg2.text = ""
                        answerArg3.text = ""
                    }
                    2 -> {
                        answerArg1.text = String.format(res.getString(R.string.argument_in_thesis_text),
                            ArgumentList.argumentList[0].statement,
                            ArgumentList.argumentList[0].clarification,
                            ArgumentList.argumentList[0].evidence,
                            ArgumentList.argumentList[0].summary)
                        answerArg2.text = String.format(res.getString(R.string.argument_in_thesis_text),
                            ArgumentList.argumentList[1].statement,
                            ArgumentList.argumentList[1].clarification,
                            ArgumentList.argumentList[1].evidence,
                            ArgumentList.argumentList[1].summary)
                        answerArg3.text = ""
                    }
                    else -> {
                        answerArg1.text = String.format(res.getString(R.string.argument_in_thesis_text),
                            ArgumentList.argumentList[0].statement,
                            ArgumentList.argumentList[0].clarification,
                            ArgumentList.argumentList[0].evidence,
                            ArgumentList.argumentList[0].summary)
                        answerArg2.text = String.format(res.getString(R.string.argument_in_thesis_text),
                            ArgumentList.argumentList[1].statement,
                            ArgumentList.argumentList[1].clarification,
                            ArgumentList.argumentList[1].evidence,
                            ArgumentList.argumentList[1].summary)
                        answerArg3.text = String.format(res.getString(R.string.argument_in_thesis_text),
                            ArgumentList.argumentList[2].statement,
                            ArgumentList.argumentList[2].clarification,
                            ArgumentList.argumentList[2].evidence,
                            ArgumentList.argumentList[2].summary)
                    }
                }
            }
            InterScreenController.chooseAnswerArg =0
        }

        val argBtn1 = promptView.findViewById<Button>(R.id.btn_answer1)
        val argBtn2 = promptView.findViewById<Button>(R.id.btn_answer2)
        val argBtn3 = promptView.findViewById<Button>(R.id.btn_answer3)

        confirm.setCancelable(false)
            .setPositiveButton("Create") { dialog, _ ->
                run {

                    if (getRoundNumber() == 0){
                        Toast.makeText(context, "Debate is over", Toast.LENGTH_SHORT).show()
                        dialog.cancel()
                    }else{
                        val newThesisRaw = ThesisJsonRaw(0,
                            stringCutter1024(thesisIntroInput.text.toString()),
                            stringCutter1024(thesisDefinitionInput.text.toString()),
                            stringCutter1024(thesisProblemInput.text.toString()),
                            stringCutter1024(thesisPlanInput.text.toString()),
                            stringCutter1024(thesisCaseIntroInput.text.toString()),
                            stringCutter1024(thesisCaseDescInput.text.toString()),
                            "",
                            getRoundNumber(),
                            thesis.id,
                            debateId,
                            CurrentUser.id,
                            Util.getCurrentDate())

                        val currentThesisId = if(thesis.id == 0L){
                            saveThesis2(newThesisRaw)
                        } else saveThesis(newThesisRaw)
                        if (ArgumentList.argumentList.isNotEmpty()){
                            ArgumentList.argumentList.forEach {
                                if (it.answer_id == null || it.answer_id == 0L || it.answer_id == Long.MAX_VALUE)
                                    saveArgumentWithoutAnswer(
                                        ArgumentJsonRaw(
                                        0,
                                        it.statement,
                                        it.clarification,
                                        it.evidence,
                                        it.summary,
                                        it.answer_id,
                                        it.debate_id,
                                        currentThesisId,
                                        CurrentUser.id,
                                        it.date_time)
                                    )
                                else saveArgument(ArgumentJsonRaw(
                                    0,
                                    it.statement,
                                    it.clarification,
                                    it.evidence,
                                    it.summary,
                                    it.answer_id,
                                    it.debate_id,
                                    currentThesisId,
                                    CurrentUser.id,
                                    it.date_time))
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

                    thesisIntroLive.postValue(thesisIntroInput.text.toString())
                    thesisDefinitionLive.postValue(thesisDefinitionInput.text.toString())
                    thesisProblemLive.postValue(thesisProblemInput.text.toString())
                    thesisPlanLive.postValue(thesisPlanInput.text.toString())
                    thesisCaseIntroLive.postValue(thesisCaseIntroInput.text.toString())
                    thesisCaseDescLive.postValue(thesisCaseDescInput.text.toString())

                    val bundle = bundleOf(Pair("debate_id", debateId))
                    navController.saveState()
                    navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)
                    dialog.cancel()
                }
                argBtn2.setOnClickListener{

                    thesisIntroLive.postValue(thesisIntroInput.text.toString())
                    thesisDefinitionLive.postValue(thesisDefinitionInput.text.toString())
                    thesisProblemLive.postValue(thesisProblemInput.text.toString())
                    thesisPlanLive.postValue(thesisPlanInput.text.toString())
                    thesisCaseIntroLive.postValue(thesisCaseIntroInput.text.toString())
                    thesisCaseDescLive.postValue(thesisCaseDescInput.text.toString())

                    InterScreenController.chooseAnswerArg = 1
                    InterScreenController.thesisPressed = thesis

                    val bundle = bundleOf(Pair("debate_id", debateId))
                    navController.saveState()
                    navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)
                    dialog.cancel()
                }
                argBtn3.setOnClickListener{

                    thesisIntroLive.postValue(thesisIntroInput.text.toString())
                    thesisDefinitionLive.postValue(thesisDefinitionInput.text.toString())
                    thesisProblemLive.postValue(thesisProblemInput.text.toString())
                    thesisPlanLive.postValue(thesisPlanInput.text.toString())
                    thesisCaseIntroLive.postValue(thesisCaseIntroInput.text.toString())
                    thesisCaseDescLive.postValue(thesisCaseDescInput.text.toString())

                    InterScreenController.chooseAnswerArg = 1
                    InterScreenController.thesisPressed = thesis

                    val bundle = bundleOf(Pair("debate_id", debateId))
                    navController.saveState()
                    navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)
                    dialog.cancel()
                }
            }
            show()
        }
    }

    //TODO сделать так, чтобы при свайпе вправо на открытом тезисе диалог закрывался.
    fun openThesis(thesis: ThesisJson, navController: NavController){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.thesis_open, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)

        promptView.setOnTouchListener(getOnSwipeTouchListener(context, navController, thesis.id))

        val textIntro = promptView.findViewById<TextView>(R.id.thesis_open_intro)
        val textDefinition = promptView.findViewById<TextView>(R.id.thesis_open_definition)
        val textProblem = promptView.findViewById<TextView>(R.id.thesis_open_problem)
        val textPlan = promptView.findViewById<TextView>(R.id.thesis_open_plan)
        val textCaseIntro = promptView.findViewById<TextView>(R.id.thesis_open_case_intro)
        val textCaseDesc = promptView.findViewById<TextView>(R.id.thesis_open_case_desc)

        val btn = promptView.findViewById<Button>(R.id.btn_answer)
        btn.setOnClickListener {
            createNewThesis(thesis, navController)
        }

        textIntro.text = thesis.intro
        textDefinition.text = thesis.definition
        textProblem.text = thesis.problem
        textPlan.text = thesis.plan
        textCaseIntro.text = thesis.caseIntro
        textCaseDesc.text = thesis.caseDesc

        confirm.create()
        confirm.show()
    }

    private fun changeRights(personRightsJson: PersonRightsJson){
        if (CurrentUser.id != debateWithPersons.first().findCreator()!!.id){
            Toast.makeText(context, "you don't allow to change users' rights ", Toast.LENGTH_SHORT).show()
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

    private fun getOnSwipeTouchListener(context: Context, navController: NavController, thesisId: Long)
    = object : OnSwipeTouchListener(context) {
        override fun onSwipeLeft() {
            val bundle = bundleOf(Pair("debate_id", debateId), Pair("thesis_id", thesisId))
            navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)
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

    private fun saveThesis2(thesis: ThesisJsonRaw): Long = runBlocking {
        saveThesisWithoutAnswerAsync(thesis).await()
    }

    private fun saveThesisAsync(thesis: ThesisJsonRaw) = async{
        return@async apiFactory.insertThesisRaw(thesis)
    }

    private fun saveThesisWithoutAnswerAsync(thesis: ThesisJsonRaw) = async{
        return@async apiFactory.insertThesisRaw2(thesis)
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

    private fun saveArgumentWithoutAnswer(argumentJsonRaw: ArgumentJsonRaw): Long = runBlocking {
        saveArgumentWithoutAnswerAsync(argumentJsonRaw).await()
    }

    private fun saveArgumentWithoutAnswerAsync(argumentJsonRaw: ArgumentJsonRaw) = async {
        return@async apiFactory.insertArgumentWithoutAnswerRaw(argumentJsonRaw)
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
            }
        }
    }

}