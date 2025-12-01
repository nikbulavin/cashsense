package ru.resodostudios.cashsense.core.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerDialogDefaults
import androidx.compose.material3.TimePickerDialogDefaults.MinHeightForTimePicker
import androidx.compose.material3.TimePickerDisplayMode
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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Schedule
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.ui.LocalTimeZone
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.math.BigDecimal
import java.time.format.FormatStyle
import java.util.Currency
import kotlin.time.Instant
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerTextField(
    timestamp: Instant,
    @StringRes labelRes: Int,
    icon: ImageVector,
    onDateSelect: (Instant) -> Unit,
    modifier: Modifier = Modifier,
    onlyFutureDates: Boolean = false,
) {
    var openDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = timestamp.formatDate(),
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(labelRes)) },
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
        val timeZone = LocalTimeZone.current
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = timestamp.toEpochMilliseconds(),
            selectableDates = if (onlyFutureDates) {
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
                        val localTime = timestamp.toLocalDateTime(timeZone)
                        val selectedDate = Instant
                            .fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                            .toLocalDateTime(timeZone)
                        val instant = LocalDateTime(
                            selectedDate.year,
                            selectedDate.month.number,
                            selectedDate.day,
                            localTime.hour,
                            localTime.minute,
                        ).toInstant(timeZone)
                        onDateSelect(instant)
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
            DatePicker(
                state = datePickerState,
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerTextField(
    timestamp: Instant,
    onTimeSelect: (Instant) -> Unit,
    modifier: Modifier = Modifier,
) {
    var openDialog by remember { mutableStateOf(false) }
    var displayMode by remember { mutableStateOf(TimePickerDisplayMode.Picker) }
    val windowInfo = LocalWindowInfo.current

    OutlinedTextField(
        value = timestamp.formatDate(DateFormatType.TIME, FormatStyle.SHORT),
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
        val timeZone = LocalTimeZone.current
        val localTime = timestamp.toLocalDateTime(timeZone)
        val timePickerState = rememberTimePickerState(
            initialHour = localTime.hour,
            initialMinute = localTime.minute,
        )
        TimePickerDialog(
            title = { Text(stringResource(localesR.string.time)) },
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        val instant = LocalDateTime(
                            localTime.year,
                            localTime.month.number,
                            localTime.day,
                            timePickerState.hour,
                            timePickerState.minute,
                        ).toInstant(timeZone)
                        onTimeSelect(instant)
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
            modeToggleButton = {
                if (windowInfo.containerSize.height.dp > MinHeightForTimePicker) {
                    TimePickerDialogDefaults.DisplayModeToggle(
                        onDisplayModeChange = {
                            displayMode = if (displayMode == TimePickerDisplayMode.Picker) {
                                TimePickerDisplayMode.Input
                            } else {
                                TimePickerDisplayMode.Picker
                            }
                        },
                        displayMode = displayMode,
                    )
                }
            },
        ) {
            if (
                displayMode == TimePickerDisplayMode.Picker &&
                windowInfo.containerSize.height.dp > MinHeightForTimePicker
            ) {
                TimePicker(state = timePickerState)
            } else {
                TimeInput(state = timePickerState)
            }
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