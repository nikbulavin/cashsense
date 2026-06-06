package ru.resodostudios.cashsense.core.designsystem.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun CsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    supportingText: String? = null,
    placeholderText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        label = {
            Text(
                text = labelText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        placeholder = placeholderText?.let { text ->
            {
                Text(
                    text = text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        supportingText = supportingText?.let { text ->
            {
                Text(
                    text = text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        singleLine = singleLine,
        modifier = modifier,
        colors = TextFieldDefaults.tonalColors(),
        shape = TextFieldDefaults.roundedShape,
    )
}