package com.capstone.mcov_naskes_v1.cloudmessage

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.capstone.mcov_naskes_v1.MainActivity
import com.capstone.mcov_naskes_v1.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CloudMessaging: FirebaseMessagingService() {
    private val firestoreDatabase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var notificationId = 1
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("token",message.data.toString())
        if(message.data.isNotEmpty()){

            val map = message.data

            val title = map["title"] as String
            val body = map["message"] as String
            val uid = map["uid"] as String
            var pendingIntent: PendingIntent

            Intent(this,MainActivity::class.java)
//                .putExtra("Flags", 1)
//                .putExtra("uid", uid )
                .also { targetIntent ->
                    PendingIntent.getActivity(this,111,targetIntent,PendingIntent.FLAG_MUTABLE)
                        .also {
                            pendingIntent = it
                        }
                }
            NotificationManagerCompat
                .from(this)
                .notify(
                    notificationId,
                    createNotification(
                        context = this,
                        title = title,
                        body = body,
                        pendingIntent = pendingIntent
                    )
                )
            notificationId++
            storeNotification(uid,title, body)
        }
    }

    private fun storeNotification(uid:String, title: String, body: String) {
        val newNotif = com.capstone.mcov_naskes_v1.models.Notification(null, uid, title, body, null)
        firestoreDatabase.collection("naskesnotifications")
            .add(newNotif)
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun createNotification(
        context: Context,
//        smallIcon: Int,
        title: String ="",
        body:String ="",
        channelId: String="channel_id",
        channelNameForAndroid0: String="channel_name",
        channelDescriptionForAndroid0: String="channel untuk notifikasi",
        pendingIntent: PendingIntent?=null,
        category: String = ""
    ):Notification{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId,channelNameForAndroid0,NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = channelDescriptionForAndroid0
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
//            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
        val builder = NotificationCompat.Builder(context, channelId)
        builder.setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.cat)
            .priority = NotificationCompat.PRIORITY_DEFAULT

        if(category!=""){
            builder.setCategory(category)
        }

        builder.setAutoCancel(true)

        pendingIntent?.let {
            builder.setContentIntent(pendingIntent)
        }
        Log.d("token","notif selesai dibuat")
        return builder.build()
    }
}