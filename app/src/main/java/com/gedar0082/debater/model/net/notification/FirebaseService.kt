package com.gedar0082.debater.model.net.notification

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
import com.gedar0082.debater.viewmodel.DebateViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.sql.SQLOutput
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        println("message data from firebase receive " + message.data)
        if(message.data["title"] == "debate"){
            println(message.data["title"] + " in if block in message receive ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;")
            NotificationEvent.serviceEvent.postValue("fuck")
        }else if (message.data["title"] == "thesis"){
            println(message.data["title"] + " in if block in message receive ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;4")
            NotificationEvent.thesisEvent.postValue("fuck")
        }else if(message.data["title"] == "argument"){
            print(message.data["title"] + " in if block in message receive ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;4")
            NotificationEvent.argumentEvent.postValue("fuck")
        }else{
            println(message.data["title"] + " in if block in message receive NOT FOUND ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;")
        }

        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }
        println("#####################################################")
        println("NOTIFICATION")

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