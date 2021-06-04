package com.gedar0082.debater.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.gedar0082.debater.R
import com.gedar0082.debater.model.net.api.ApiFactory
import com.gedar0082.debater.model.net.pojo.BodyMessageJson
import com.gedar0082.debater.model.net.pojo.PersonAuthenticationJson
import com.gedar0082.debater.model.net.pojo.PersonJson
import com.gedar0082.debater.util.CurrentUser
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class LoginViewModel : ViewModel(), CoroutineScope, Observable {

    @Bindable val nicknameText = MutableLiveData<String>()
    @Bindable val emailText = MutableLiveData<String>()
    @Bindable val password = MutableLiveData<String>()
    @Bindable val diagnosticText = MutableLiveData<String>()
    lateinit var prefs : SharedPreferences
    lateinit var navController: NavController
    val loginLiveData = MutableLiveData<String>()
    val registrationLiveData = MutableLiveData<String>()
    val exceptionLiveData = MutableLiveData<String>()

    companion object ActPressed{
        var registrationPressed = false
        var loginPressed = false
    }


    override val coroutineContext: CoroutineContext
        get() = Job()

    private val apiFactory = ApiFactory.service


    fun login(){
        if (!ActPressed.loginPressed){
            loginLiveData.postValue("login")
            ActPressed.loginPressed = true
        }else{
            try {
                loginAct()
            }catch (e: Exception){
                e.printStackTrace()
                exceptionLiveData.postValue("exception")
            }

        }
    }

    fun registration(){
        if (!ActPressed.registrationPressed){
            registrationLiveData.postValue("registration")
            ActPressed.registrationPressed = true
        }else{
            try {
                registrationAct()
            }catch (e: Exception){
                e.printStackTrace()
                exceptionLiveData.postValue("exception")
            }

        }

    }

    private fun loginAct() {
        val paj = getPersonAuthenticationJson()
        val request = checkAuthentication(paj)
        if (request) {
            savePrefs()
            println("successfully authentication")
            getPerson(paj)
            navigationToDebate()
        } else {
            println("something went wrong")
        }
    }

    private fun registrationAct(){
        val paj = getPersonAuthenticationJson()
        val request = checkRegistration(paj)
        if (request){
            val person = savePerson(paj)
            savePrefs()
            saveCurrentUser(person)
            navigationToDebate()
            println("successfully registration")
        }else{
            println("something went wrong")
        }
    }

    private fun getPersonAuthenticationJson() : PersonAuthenticationJson{
        return PersonAuthenticationJson(
            nicknameText.value!!,
            emailText.value!!,
            password.value!!,
        )
    }

    private fun checkAuthentication(personAuthenticationJson: PersonAuthenticationJson) : Boolean = runBlocking {
        authenticationAsync(personAuthenticationJson).await()
    }

    private fun checkRegistration(personAuthenticationJson: PersonAuthenticationJson) : Boolean = runBlocking {
        registrationAsync(personAuthenticationJson).await()
    }

    private fun authenticationAsync(personAuthenticationJson: PersonAuthenticationJson) = async {
        val response: Response<BodyMessageJson> = apiFactory.checkAuthentication(personAuthenticationJson)
        checkResponse(response)
        return@async response.isSuccessful
    }

    private fun registrationAsync(personAuthenticationJson: PersonAuthenticationJson) = async {
        val response: Response<BodyMessageJson> = apiFactory.checkRegistration(personAuthenticationJson)
        checkResponse(response)
        return@async response.isSuccessful
    }

    private fun savePerson(personAuthentication : PersonAuthenticationJson) : PersonJson = runBlocking{
        savePersonAsync(personAuthentication).await()
    }

    private fun savePersonAsync(personAuthenticationJson: PersonAuthenticationJson) = async{
        return@async apiFactory.insertPerson(personAuthenticationJson)
    }

    private fun checkResponse(response: Response<BodyMessageJson>){
        if(response.isSuccessful){
            diagnosticText.postValue("Successful")
        } else{
            val jsonObject = JSONObject(response.errorBody()!!.string())
            val st = jsonObject.getString("message")
            diagnosticText.postValue(st)
        }
    }

    private fun savePrefs(){
        prefs.edit().putString("nickname", nicknameText.value!!)?.apply()
        prefs.edit().putString("email", emailText.value!!)?.apply()
        prefs.edit().putString("password", password.value!!)?.apply()
    }

    private fun navigationToDebate(){
        navController.navigate(R.id.action_loginFragment_to_debateFragment)
    }

    private fun saveCurrentUser(personJson: PersonJson){
        personJson.let {
            CurrentUser.id = it.id
            CurrentUser.nickname = it.nickname
            CurrentUser.email = it.email
            CurrentUser.password = it.password
        }
    }

    private fun getPerson(paj : PersonAuthenticationJson){
        launch {
            runCatching { apiFactory.getPersonByEmail(paj.email) }.onSuccess {
                Log.e("person", it.toString())
                CurrentUser.id = it.id
                CurrentUser.nickname = it.nickname
                CurrentUser.email = it.email
                CurrentUser.password = it.password
            }.onFailure {
                it.printStackTrace()
                println("failed to get user")
            }
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}