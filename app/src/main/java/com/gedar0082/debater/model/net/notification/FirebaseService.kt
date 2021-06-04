package com.gedar0082.debater.model.net.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.gedar0082.debater.R
import com.gedar0082.debater.view.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

/**
 * Firebase service realization.
 * It is necessary to subscribe in the fragment class at the beginning of the life cycle.
 * If there is a need to unsubscribe, so that there are no notifications when the application
 * is not on the screen, unsubscribe at the end of the life cycle
 */

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseService : FirebaseMessagingService() {

    /**
     * The alert message has a date field. When sending an alert, the information necessary
     * for parsing the incoming alert is put there. Here, the alert information is viewed and,
     * depending on the data, a branch is selected in when
     *
     * The NotificationEvent class contains a LiveData, updating which in a specific context starts
     * the process of receiving updated information about the debate.
     * Therefore, in the thread, when receiving a notification, a post is made to this LiveData
     */
    override fun onMessageReceived(message: RemoteMessage) {
        println("message data from firebase receive " + message.data)
        when {
            message.data["title"] == "debate" -> {
                NotificationEvent.serviceEvent.postValue("message")
            }
            message.data["title"] == "thesis" -> {
                NotificationEvent.thesisEvent.postValue("message")
            }
            message.data["title"] == "argument" -> {
                NotificationEvent.argumentEvent.postValue("message")
            }
            else -> {
                println(message.data["title"] + " in if block in message receive NOT FOUND")
            }
        }

        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)  // возможно при переходе по уведомлению будет открываться главное окно
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setSmallIcon(R.drawable.ic_add)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(notificationID, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "channel_name"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}