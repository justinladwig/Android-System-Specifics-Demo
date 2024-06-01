package com.moco.androidSpecifics

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moco.androidSpecifics.notificationSchedule.scheduleNotification
import com.moco.androidSpecifics.ui.theme.MyApplicationTheme

private val permissionToRequest: Array<String>
    get() {
        var permissions = emptyArray<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }
        return permissions
    }

class MainActivity : ComponentActivity() {
    private var channelID1 = "default_notifications"
    private var channelID2 = "alarm_notifications"
    private var notificationID = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NotificationOverview(modifier = Modifier.padding(it))
                }
            }
        }
    }

    @Preview(widthDp = 300)
    @Composable
    fun NotificationOverview(
        modifier: Modifier = Modifier
    ) {
        val viewModel = viewModel<MainViewModel>()
        val dialogQueue = viewModel.visiblePermissionDialogQueue

        val multipleRequestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            permissionToRequest.forEach { permission ->
                viewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                viewModel.onPermissionResult(
                    permission = Manifest.permission.SCHEDULE_EXACT_ALARM,
                    isGranted = alarmManager.canScheduleExactAlarms()
                )
            }
        }
        Column(
            modifier = modifier
                .padding(30.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedButton(
                onClick = {
                    multipleRequestPermissionLauncher.launch(
                        permissionToRequest
                    )
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.button_request_permission),
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    sendNotification1()
                },
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.button_text),
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = {
                    sendNotification2()
                },
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.button_text_2),
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = {
                    sendNotification3()
                },
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.button_text_3),
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = {
                    sendNotification4()
                },
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.button_text_5),
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        scheduleNotification(true)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.button_text_4),
                    textAlign = TextAlign.Center
                )
            }

        }

        dialogQueue
            .reversed()
            .forEach { permission ->
                if (permission == Manifest.permission.SCHEDULE_EXACT_ALARM) {
                    PermissionDialog(
                        permissionTextProvider = ExactAlarmsPermissionTextProvider(),
                        onDismiss = viewModel::dismissDialog,
                        onOkClick = viewModel::dismissDialog,
                        onGoToAppSettingsClick = ::openAppSettings
                    )

                }
                PermissionDialog(
                    permissionTextProvider = when (permission) {
                        Manifest.permission.POST_NOTIFICATIONS -> NotificationPermissionTextProvider()
                        else -> return@forEach
                    },
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission),
                    onDismiss = viewModel::dismissDialog,
                    onOkClick = {
                        viewModel.dismissDialog()
                        multipleRequestPermissionLauncher.launch(
                            arrayOf(permission)
                        )
                    },
                    onGoToAppSettingsClick = ::openAppSettings
                )
            }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID1, name, importance)

            val descriptionText = getString(R.string.default_notification_description)
            channel.description = descriptionText

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val name2 = getString(R.string.alarm_notification_channel_name)
            val importance2 = NotificationManager.IMPORTANCE_HIGH
            val channel2 = NotificationChannel(channelID2, name2, importance2).apply {
                setSound(null, null)
            }

            val notificationManager2 =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager2.createNotificationChannel(channel2)
        }
    }

    private fun sendNotification1() {
        val builder = NotificationCompat.Builder(this, channelID1)
            .setSmallIcon(R.drawable.baseline_crisis_alert_24)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationID, builder.build())
            }
        }
    }

    private fun sendNotification2() {
        val contentIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(this, channelID1)
            .setSmallIcon(R.drawable.baseline_crisis_alert_24)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationID, builder.build())
            }
        }
    }

    private fun sendNotification3() {
        val contentIntent1 = Intent(Intent.ACTION_VIEW, Uri.parse("https://moxd.io"))
        val pendingIntent1 = PendingIntent.getActivity(
            this,
            0,
            contentIntent1,
            PendingIntent.FLAG_IMMUTABLE
        )
        val contentIntent2 = Intent(this, MainActivity::class.java)
        val pendingIntent2 = PendingIntent.getActivity(
            this,
            0,
            contentIntent2,
            PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(this, channelID1)
            .setSmallIcon(R.drawable.baseline_crisis_alert_24)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent2)
            .addAction(R.drawable.baseline_flash_on_24, getString(R.string.open_moxd), pendingIntent1)
            .addAction(R.drawable.baseline_flash_on_24, getString(R.string.open_app), pendingIntent2)
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationID, builder.build())
            }
        }
    }

    private fun sendNotification4() {
        val builder = NotificationCompat.Builder(this, channelID1)
            .setSmallIcon(R.drawable.baseline_crisis_alert_24)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(100, 50, false)
            .setWhen(System.currentTimeMillis() + 300000)
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationID, builder.build())
            }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)

}