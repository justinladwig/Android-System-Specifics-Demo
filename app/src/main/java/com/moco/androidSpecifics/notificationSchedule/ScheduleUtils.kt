package com.moco.androidSpecifics.notificationSchedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.widget.Toast
import java.util.concurrent.TimeUnit

private const val SCHEDULE_TIME = 5L

fun Context.scheduleNotification(isLockScreen: Boolean) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val timeInMillis = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(SCHEDULE_TIME)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(this, "Permission is denied", Toast.LENGTH_SHORT).show()
            return
        }
    }
    with(alarmManager) {
        setExact(AlarmManager.RTC_WAKEUP, timeInMillis, getReceiver(isLockScreen))
    }
}

fun Context.getReceiver(isLockScreen: Boolean): PendingIntent {
    return PendingIntent.getBroadcast(
        this,
        0,
        NotificationReceiver.build(this, isLockScreen),
        PendingIntent.FLAG_IMMUTABLE
    )
}