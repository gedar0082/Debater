package com.gedar0082.debater.model.net.notification

import androidx.lifecycle.MutableLiveData

object NotificationEvent {

    val serviceEvent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val thesisEvent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val argumentEvent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

}