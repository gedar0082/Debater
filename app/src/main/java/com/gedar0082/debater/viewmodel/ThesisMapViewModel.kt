package com.gedar0082.debater.viewmodel


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.gedar0082.debater.R
import com.gedar0082.debater.model.local.entity.Argument
import com.gedar0082.debater.model.local.entity.DebateWithTheses
import com.gedar0082.debater.model.local.entity.Thesis
import com.gedar0082.debater.repository.ArgumentRepository
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import com.gedar0082.debater.util.InterScreenController
import com.gedar0082.debater.util.OnSwipeTouchListener
import kotlinx.coroutines.*


class ThesisMapViewModel(
    private val thesisRepository: ThesisRepository,
    private val debateRepository: DebateRepository,
    private val argumentRepository: ArgumentRepository
) : ViewModel(), Observable {

//    lateinit var clickableThesis: Thesis

    lateinit var context: Context
    var theses = MutableLiveData<List<DebateWithTheses>>()
    var debateId: Long = 0

    @Bindable
    val thesisName = MutableLiveData<String>()
    @Bindable
    val thesisDesc = MutableLiveData<String>()
    @Bindable
    val arg1Text = MutableLiveData<String>()

    init {
        getTheses(debateId)
    }


    fun getTheses(id: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            val the = async(Dispatchers.IO) {
                return@async debateRepository.getDebateWithTheses(id)
            }.await()
            withContext(Dispatchers.Main) {
                theses.value = the
            }
        }
    }


    fun createNewThesis(thesis: Thesis, navController: NavController) {
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
            InterScreenController.argumentPressed?.let { answerArg1.text = it.argText }
            InterScreenController.chooseAnswerArg =0
        }

        val btn = promptView.findViewById<Button>(R.id.btn_answer1)
        confirm.setCancelable(false)
            .setPositiveButton("Create") { dialog, _ ->
                run {

                    thesisName.value = text1.text.toString()
                    thesisDesc.value = text2.text.toString()
                    arg1Text.value = arg1.text.toString()
                    insertThesis(thesis)
                    insertArgument(InterScreenController.argumentPressed, thesis)
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
                    println("ghjkl;")
                    val bundle = bundleOf(Pair("debate_id", debateId))
                    navController.saveState()
                    navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)
                    dialog.cancel()
                }
            }
            show()
        }

    }

    fun openThesis(thesis: Thesis, navController: NavController){
        val confirm = AlertDialog.Builder(context, R.style.myDialogStyle)
        val li = LayoutInflater.from(context)
        val promptView: View = li.inflate(R.layout.thesis_open, null)
        confirm.setView(promptView)
        confirm.setCancelable(true)
        promptView.setOnTouchListener(object: OnSwipeTouchListener(context){
            override fun onSwipeLeft() {
                val bundle = bundleOf(Pair("debate_id", debateId))
                navController.navigate(R.id.action_thesisMapFragment_to_argumentMapFragment, bundle)

            }
        })
        val textName = promptView.findViewById<TextView>(R.id.thesis_name)
        val textDesc = promptView.findViewById<TextView>(R.id.thesis_desc)
        val btn = promptView.findViewById<Button>(R.id.btn_answer)
        btn.setOnClickListener {
            createNewThesis(thesis, navController)
        }
        textName.text = thesis.thesisName
        textDesc.text = thesis.thesisIntro
        confirm.create()
        confirm.show()
    }



    private fun insertThesis(thesis: Thesis): Job = viewModelScope.launch {
        thesisRepository.insert(Thesis(0, debateId, thesis.tId, thesisName.value!!, thesisDesc.value!!, null, null, null, null, null))
        getTheses(debateId)
    }

    private fun insertArgument(argument: Argument?, thesis: Thesis): Job = viewModelScope.launch {
        argumentRepository.insert(Argument(0, argument?.aId?: 0, thesis.tId, debateId, arg1Text.value!!))
    }


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}