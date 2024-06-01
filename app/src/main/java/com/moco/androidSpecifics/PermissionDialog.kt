package com.moco.androidSpecifics

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PermissionDialog(
    modifier: Modifier = Modifier,
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean = true,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Text(
                text = if (isPermanentlyDeclined) {
                    stringResource(id = R.string.grant_permission)
                } else {
                    stringResource(id = R.string.ok)
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isPermanentlyDeclined) {
                            onGoToAppSettingsClick()
                        } else {
                            onOkClick()
                        }
                    },
            )
        },
        title = {
            Text(text = stringResource(id = R.string.permission_required))
        },
        text = {
            Text(
                text = permissionTextProvider.getDescription(LocalContext.current, isPermanentlyDeclined)
            )
        },
        modifier = modifier,
    )
}

interface PermissionTextProvider {
    fun getDescription(context: Context, isPermanentlyDeclined: Boolean): String
}

class NotificationPermissionTextProvider: PermissionTextProvider {
    override fun getDescription(context: Context, isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            context.getString(R.string.notification_permanently_declined)
        } else {
            context.getString(R.string.notification_permission_description)
        }
    }
}

class ExactAlarmsPermissionTextProvider: PermissionTextProvider {
    override fun getDescription(context: Context, isPermanentlyDeclined: Boolean): String {
        return context.getString(R.string.exact_alarms_permanently_declined)
    }
}