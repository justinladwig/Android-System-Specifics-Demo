package com.moco.androidSpecifics

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


var channelID = "alarm_notifications"
var notificationID = 555

fun Context.sendNotificationWithFullScreenIntent(
) {

    val contentIntent = Intent(this, AlarmActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        this,
        0,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val contentIntent1 = Intent(this, MainActivity::class.java)
    val pendingIntent1 = PendingIntent.getActivity(
        this,
        0,
        contentIntent1,
        PendingIntent.FLAG_IMMUTABLE
    )
    val builder = NotificationCompat.Builder(this, channelID)
        .setSmallIcon(R.drawable.baseline_crisis_alert_24)
        .setContentTitle(getString(R.string.notification_title))
        .setContentText(getString(R.string.notification_text))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setFullScreenIntent(pendingIntent, true)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.baseline_flash_on_24, getString(R.string.expand), pendingIntent)
        .addAction(R.drawable.baseline_flash_on_24, getString(R.string.end_alarm), pendingIntent1)
        .setSound(null)
    with(NotificationManagerCompat.from(this)) {
        if (ActivityCompat.checkSelfPermission(
                this@sendNotificationWithFullScreenIntent,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notify(notificationID, builder.build())
        }
    }
}