package ru.resodostudios.cashsense.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

@Composable
fun CsAlertDialog(
    @StringRes titleRes: Int,
    @StringRes confirmButtonTextRes: Int,
    @StringRes dismissButtonTextRes: Int,
    icon: ImageVector,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isConfirmEnabled: Boolean = true,
    content: @Composable (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = {},
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        },
        title = {
            Text(
                text = stringResource(titleRes),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = content,
        confirmButton = {
            val hapticFeedback = LocalHapticFeedback.current
            Button(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    onConfirm()
                },
                enabled = isConfirmEnabled,
            ) {
                Text(stringResource(confirmButtonTextRes))
            }
        },
        dismissButton = {
            TextButton(onDismiss) {
                Text(stringResource(dismissButtonTextRes))
            }
        },
        modifier = modifier,
    )
}