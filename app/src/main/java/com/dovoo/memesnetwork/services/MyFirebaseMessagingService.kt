package com.dovoo.memesnetwork.services


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dovoo.memesnetwork.DefaultActivity
import com.dovoo.memesnetwork.R
import com.dovoo.memesnetwork.model.Notification
import com.dovoo.memesnetwork.model.SetFirebaseTokenRequest
import com.dovoo.memesnetwork.network.MemesRestAdapter
import com.dovoo.memesnetwork.utils.GlobalFunc
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (GlobalFunc.isLogin(applicationContext)) {

            if (remoteMessage.data.isNotEmpty()) {
                Log.d(TAG, "Message data payload: ${remoteMessage.data}")
                handleNow(remoteMessage.data)
            }

//            // Check if message contains a notification payload.
//            remoteMessage.notification?.let {
//                Log.d(TAG, "Message Notification Body: ${it.body}")
//            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        sendRegistrationToServer(token)
    }


    private fun handleNow(data: Map<String, String>) {
        sendNotification(data)
    }

    private fun sendRegistrationToServer(token: String) {
        if (GlobalFunc.isLogin(applicationContext)) {
            val userId = GlobalFunc.getLoggedInUserId(applicationContext)
            val request = SetFirebaseTokenRequest(userId, token)
            MemesRestAdapter.apiRestService.setFirebaseToken(request)
        }
    }

    private fun sendNotification(
        data: Map<String, String>
    ) {
        GlobalFunc.addNotifCount(this)
        val title = data["title"]
        val messages = data["messages"]
        val iconUrl = data["iconUrl"]
        val notifType = data["notifType"]
        val memeId = data["memeId"]
        val userId = data["userId"]
        val commentId = data["commentId"]

        val intent = Intent(this, DefaultActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("title", title)
        intent.putExtra("messages", messages)
        intent.putExtra("iconUrl", iconUrl)
        intent.putExtra("notifType", notifType)
        intent.putExtra("memeId", memeId)
        intent.putExtra("userId", userId)
        intent.putExtra("commentId", commentId)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = when {
            notifType.equals(Notification.TYPE_FOLLOWING) -> getString(R.string.channel_id_following_notif)
            notifType.equals(Notification.TYPE_MEME_COMMENT) -> getString(R.string.channel_id_comment_notif)
            notifType.equals(Notification.TYPE_SUB_COMMENT) -> getString(R.string.channel_id_comment_notif)
            else -> ""
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var bitmap: Bitmap? = null
        try {
            bitmap = Glide.with(this)
                .asBitmap()
                .load(iconUrl)
                .submit(100, 100)
                .get()
        }catch (ex: Exception){
            if(notifType.equals(Notification.TYPE_FOLLOWING)) {
                bitmap = Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.funny_user2)
                    .submit(100, 100)
                    .get()
            }
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_memes_notif)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        if(!messages.isNullOrEmpty()) notificationBuilder.setContentText(messages)
        if(bitmap != null) notificationBuilder.setLargeIcon(bitmap)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelId,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun showNotif(){

    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}