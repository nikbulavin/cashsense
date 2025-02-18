package ru.resodostudios.cashsense.core.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import kotlinx.datetime.toKotlinInstant
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Schedule
import ru.resodostudios.cashsense.core.ui.util.FormatDateType
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.math.BigDecimal
import java.util.Calendar
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    value: String,
    @StringRes labelTextId: Int,
    icon: ImageVector,
    onDateClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    initialSelectedDateMillis: Long? = null,
    isAllDatesEnabled: Boolean = true,
) {
    var openDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(labelTextId)) },
        trailingIcon = {
            IconButton(onClick = { openDialog = true }) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            }
        },
        singleLine = true,
        modifier = modifier,
    )
    if (openDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis,
            selectableDates = if (!isAllDatesEnabled) {
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                        utcTimeMillis >= System.currentTimeMillis()

                    override fun isSelectableYear(year: Int): Boolean = true
                }
            } else DatePickerDefaults.AllDates,
        )
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        onDateClick(datePickerState.selectedDateMillis!!)
                    },
                    enabled = confirmEnabled.value,
                ) {
                    Text(stringResource(localesR.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog = false }
                ) {
                    Text(stringResource(localesR.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerTextField(
    onTimeSelect: (TimePickerState) -> Unit,
    modifier: Modifier = Modifier,
    timestamp: Calendar = Calendar.getInstance(),
) {
    var openDialog by remember { mutableStateOf(false) }
    val time = timestamp
        .toInstant()
        .toKotlinInstant()
        .formatDate(FormatDateType.TIME)

    OutlinedTextField(
        value = time,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(localesR.string.time)) },
        trailingIcon = {
            IconButton(onClick = { openDialog = true }) {
                Icon(
                    imageVector = CsIcons.Outlined.Schedule,
                    contentDescription = null,
                )
            }
        },
        singleLine = true,
        modifier = modifier,
    )
    if (openDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = timestamp.get(Calendar.HOUR_OF_DAY),
            initialMinute = timestamp.get(Calendar.MINUTE),
        )
        TimePickerDialog(
            title = { Text(stringResource(localesR.string.time)) },
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        onTimeSelect(timePickerState)
                    },
                ) {
                    Text(stringResource(localesR.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog = false }
                ) {
                    Text(stringResource(localesR.string.cancel))
                }
            },
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
fun OutlinedAmountField(
    value: String,
    onValueChange: (String) -> Unit,
    currency: Currency,
    @StringRes labelRes: Int,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Unspecified,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val isSuffixEnabled = BigDecimal.ONE
        .formatAmount(currency)
        .first()
        .isDigit()

    OutlinedTextField(
        value = value,
        textStyle = if (isSuffixEnabled) {
            LocalTextStyle.current.copy(textAlign = TextAlign.End)
        } else {
            LocalTextStyle.current
        },
        onValueChange = { onValueChange(it.cleanAmount()) },
        label = { Text(stringResource(labelRes)) },
        placeholder = {
            Text(
                text = "0.01",
                textAlign = if (isSuffixEnabled) TextAlign.End else TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
        prefix = if (!isSuffixEnabled) {
            {
                Text(currency.symbol)
            }
        } else {
            null
        },
        suffix = if (isSuffixEnabled) {
            {
                Text(currency.symbol)
            }
        } else {
            null
        },
        modifier = modifier,
    )
}