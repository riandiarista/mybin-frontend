package com.example.mybin.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.mybin.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val sharedPref = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val jwt = sharedPref.getString("token", null)

        if (jwt != null) {

            val api = ApiClient.instance
            api.updateFCMToken("Bearer $jwt", FCMRequest(token)).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("FCM", "Token terupdate di server")
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {}
            })
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            val channelId = "mybin_notif"
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "MyBin", NotificationManager.IMPORTANCE_DEFAULT)
                manager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(it.title)
                .setContentText(it.body)
                .setAutoCancel(true)

            manager.notify(0, builder.build())
        }
    }
}