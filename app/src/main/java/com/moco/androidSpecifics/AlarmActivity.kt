package com.moco.androidSpecifics

import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moco.androidSpecifics.notificationSchedule.turnScreenOffAndKeyguardOn
import com.moco.androidSpecifics.notificationSchedule.turnScreenOnAndKeyguardOff
import com.moco.androidSpecifics.ui.theme.MyApplicationTheme

class AlarmActivity : ComponentActivity() {
    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        turnScreenOnAndKeyguardOff()
        setContent {
            MyApplicationTheme {
                NotificationScreen()
            }
        }
        startAlarm()
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

    @Preview
    @Composable
    fun NotificationScreen(modifier: Modifier = Modifier) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.Blue)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    "Alarm!",
                    fontSize = 80.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 40.dp)
                )
                Button(
                    onClick = {
                        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                            cancel(555)
                        }
                        finish()
                    },
                    colors = ButtonDefaults.buttonColors(Color.White, Color.Black),
                    modifier = Modifier
                        .padding(bottom = 120.dp)
                ) {
                    Text(text = stringResource(id = R.string.end_alarm))
                }
            }
        }
    }

    private fun startAlarm() {
        mMediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(this@AlarmActivity, Uri.parse("android.resource://" + packageName + "/" + R.raw.alarm))
            isLooping = true
        }
        mMediaPlayer?.prepare()
        mMediaPlayer?.start()
    }
}