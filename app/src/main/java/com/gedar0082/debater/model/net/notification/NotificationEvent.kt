package com.gedar0082.debater.model.net.notification

import androidx.lifecycle.MutableLiveData


/**
 * Depending on the content of the message, a specific LiveData is updated, which is subscribed to
 * in the corresponding fragments. Inside the subscription, the handler loads the updated data
 */
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