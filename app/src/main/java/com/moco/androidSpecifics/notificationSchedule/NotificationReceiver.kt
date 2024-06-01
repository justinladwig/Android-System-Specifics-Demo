package com.moco.androidSpecifics.notificationSchedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.moco.androidSpecifics.sendNotificationWithFullScreenIntent

private const val LOCK_SCREEN_KEY = "lockScreenKey"

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.getBooleanExtra(LOCK_SCREEN_KEY, true)) {
            context.sendNotificationWithFullScreenIntent()
        } else {
            context.sendNotificationWithFullScreenIntent()
        }
    }

    companion object {
        fun build(context: Context, isLockScreen: Boolean): Intent {
            return Intent(context, NotificationReceiver::class.java).also {
                it.putExtra(LOCK_SCREEN_KEY, isLockScreen)
            }
        }
    }
}