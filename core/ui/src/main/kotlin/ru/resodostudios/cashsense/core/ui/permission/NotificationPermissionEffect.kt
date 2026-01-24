package ru.resodostudios.cashsense.core.ui.permission

import android.Manifest
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun NotificationPermissionEffect(
    shouldRequestPermission: Boolean = false,
) {
    if (LocalInspectionMode.current) return
    if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) return

    val notificationsPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS,
    )

    LaunchedEffect(notificationsPermissionState, shouldRequestPermission) {
        val status = notificationsPermissionState.status
        if (status is PermissionStatus.Denied && !status.shouldShowRationale && shouldRequestPermission) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}