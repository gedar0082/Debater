package com.gedar0082.debater.viewmodel


import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.os.bundleOf
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.gedar0082.debater.R
import com.gedar0082.debater.model.net.api.ApiFactory
import com.gedar0082.debater.model.net.api.NotificationObject
import com.gedar0082.debater.model.net.notification.NotificationData
import com.gedar0082.debater.model.net.notification.PushNotification
import com.gedar0082.debater.model.net.pojo.DebateJson
import com.gedar0082.debater.model.net.pojo.PersonJson
import com.gedar0082.debater.model.net.pojo.ThesisJson
import com.gedar0082.debater.model.net.pojo.ThesisJsonRaw
import com.gedar0082.debater.util.CurrentUser
import com.gedar0082.debater.util.InterScreenController
import com.gedar0082.debater.util.OnSwipeTouchListener
import com.gedar0082.debater.util.Util
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext



class ThesisMapViewModel : ViewModel(), Observable, CoroutineScope {


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }

    @SuppressWarnings("StaticFieldLeak")
    lateinit var context: Context
    var theses = MutableLiveData<List<ThesisJson>>()
    var debateId: Long = 0
    lateinit var topic : String

    override val coroutineContext: CoroutineContext
        get() = Job()

    @Bindable
    val thesisName = MutableLiveData<String>()
    @Bindable
    val thesisDesc = MutableLiveData<String>()
    @Bindable
    val arg1Text = MutableLiveData<String>()

    private val apiFactory = ApiFactory.service
    private val notificationObject = NotificationObject.service

    fun getTheses(id: Long){
        launch {
            kotlin.runCatching { apiFactory.getThesesByDebateId(id) }.onSuccess {
//                println("################################################")
//                println("finding theses = $it")
                theses.postValue(it)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun saveThesis(thesis: ThesisJsonRaw) : Long = runBlocking{
//        println("save thesis before async")
        saveThesisAsync(thesis).await()
    }

    private fun saveThesis2(thesis: ThesisJsonRaw): Long = runBlocking {
//        println("save thesis before async")
        saveThesisAsync2(thesis).await()
    }

    private fun saveThesisAsync(thesis: ThesisJsonRaw) = async{
//        println("saveThesisAsync")
        return@async apiFactory.insertThesisRaw(thesis)
    }

    private fun saveThesisAsync2(thesis: ThesisJsonRaw) = async{
//        println("saveThesisAsync")
        return@async apiFactory.insertThesisRaw2(thesis)
    }

    private fun getDebateById(id: Long) : DebateJson = runBlocking {
        getDebateByIdAsync(id).await()
    }

    private fun getDebateByIdAsync(id: Long) = async{
        return@async apiFactory.getDebateById(id)
    }



    fun createNewThesis(thesis: ThesisJson, navController: NavController) {
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.dialog_new_thesis, null)
        confirm.setView(promptView)
        val text1: EditText = promptView.findViewById(R.id.disName)
        val text2: EditText = promptView.findViewById(R.id.disDescription)
        val arg1: EditText = promptView.findViewById(R.id.arg1)
        val answerArg1: TextView = promptView.findViewById(R.id.text_answer_for1)
        if (InterScreenController.chooseAnswerArg == 3){
            text1.setText(thesisName.value?: "")
            text2.setText(thesisDesc.value?: "")
            arg1.setText(arg1Text.value?: "")
            InterScreenController.argumentPressed?.let { answerArg1.text = it.statement }
            InterScreenController.chooseAnswerArg =0
        }

        val btn = promptView.findViewById<Button>(R.id.btn_answer1)
        confirm.setCancelable(false)
            .setPositiveButton("Create") { dialog, _ ->
                run {

                    thesisName.value = text1.text.toString()
                    thesisDesc.value = text2.text.toString()
                    arg1Text.value = arg1.text.toString()
                    val newThesisRaw = ThesisJsonRaw(0, thesisName.value!!, thesisDesc.value!!,
                        "", "", "", "", "",
                        1, thesis.id, debateId, CurrentUser.id, Util.getCurrentDate())
                    if(thesis.id == 0L)  saveThesis2(newThesisRaw) else saveThesis(newThesisRaw)
                    getTheses(debateId)
                    sendNotification(newNotification())
                    dialog.cancel()
                }
            }
        confirm.setNegativeButton("Dismiss"){ dialog, _ ->
            dialog.cancel()
        }
        confirm.create().apply {
            setOnShowListener { dialog ->
                btn.setOnClickListener{
                    InterScreenController.chooseAnswerArg = 1
                    InterScreenController.thesisPressed = thesis
                    thesisName.value = text1.text.toString()
                    thesisDesc.value = text2.text.toString()
                    arg1Text.value = arg1.text.toString()
//                    println("ghjkl;")
                    val bundle = bundleOf(Pair("debate_id", debateId))
                    navController.saveState()
                    navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)
                    dialog.cancel()
                }
            }
            show()
        }

    }

    fun openThesis(thesis: ThesisJson, navController: NavController){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.thesis_open, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)
        promptView.setOnTouchListener(object: OnSwipeTouchListener(context){
            override fun onSwipeLeft() {
                val bundle = bundleOf(Pair("debate_id", debateId), Pair("thesis_id", thesis.id))
                navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)

            }
        })
        val textName = promptView.findViewById<TextView>(R.id.thesis_name)
        val textDesc = promptView.findViewById<TextView>(R.id.thesis_desc)
        val btn = promptView.findViewById<Button>(R.id.btn_answer)
        btn.setOnClickListener {
            createNewThesis(thesis, navController)
        }
        textName.text = thesis.intro
        textDesc.text = thesis.intro
        confirm.create()
        confirm.show()
    }

    private fun newNotification(): PushNotification{
        println("NOTIFICATION")
        val notificationData = NotificationData("thesis", "pisku")
        println("@@@@@@@@@@@@@@2 topic in notification: $topic")
        return PushNotification(notificationData, topic)
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
                Log.e("notification Error", "$st")
                Log.e("notification Error", "${response.code()}")
            }
        }
    }



}