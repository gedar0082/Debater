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
        NotificationEvent.serviceEvent.postValue("fuck")
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
            .setContentTitle(message.data["type"])
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