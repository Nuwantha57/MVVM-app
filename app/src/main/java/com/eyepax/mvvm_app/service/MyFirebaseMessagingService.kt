package com.eyepax.mvvm_app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.eyepax.mvvm_app.R
import com.eyepax.mvvm_app.view.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM_Service"
        private const val CHANNEL_ID = "fcm_default_channel"
        private const val CHANNEL_NAME = "FCM Notifications"
    }

    // Called when a new FCM token is generated
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token: $token")

        // Send token to your server if needed
        sendTokenToServer(token)
    }

    // Called when a message is received
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        // Check if message contains notification payload
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Notification Title: ${notification.title}")
            Log.d(TAG, "Notification Body: ${notification.body}")

            showNotification(
                notification.title ?: "New Notification",
                notification.body ?: "You have a new message"
            )
        }

        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataPayload(remoteMessage.data)
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for FCM notifications"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open MainActivity when notification is clicked
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)  // You need to create this icon
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun handleDataPayload(data: Map<String, String>) {
        // Handle custom data payload
        val feature = data["feature"]
        val action = data["action"]

        Log.d(TAG, "Feature: $feature, Action: $action")

        // You can trigger different actions based on the data
        when (feature) {
            "BLE" -> {
                // Handle BLE-related notification
                showNotification("BLE Update", data["message"] ?: "BLE notification")
            }
            "Payment" -> {
                // Handle payment-related notification
                showNotification("Payment Update", data["message"] ?: "Payment notification")
            }
            "WiFi" -> {
                // Handle WiFi-related notification
                showNotification("WiFi Update", data["message"] ?: "WiFi notification")
            }
        }
    }

    private fun sendTokenToServer(token: String) {
        // TODO: Send token to your backend server for storing
        Log.d(TAG, "Sending token to server: $token")
    }
}
