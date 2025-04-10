package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.util.getValidCurrencies
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdownMenu(
    currency: Currency,
    onCurrencyClick: (Currency) -> Unit,
    modifier: Modifier = Modifier,
    dropDownHeight: Dp = 200.dp,
    enabled: Boolean = true,
) {
    var selectedCurrency by rememberSaveable { mutableStateOf<Currency?>(null) }
    var currencySearchText by rememberSaveable { mutableStateOf("") }

    val currencies = getValidCurrencies()
    val filteredCurrencies = currencies.filter {
        it.currencyCode.contains(currencySearchText, ignoreCase = true) ||
                it.displayName.contains(currencySearchText, ignoreCase = true)
    }

    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(currency) {
        currencySearchText = currency.currencyCode
        if (currency in currencies) {
            selectedCurrency = currency
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            value = currencySearchText,
            singleLine = true,
            onValueChange = { newText ->
                currencySearchText = newText
                expanded = true
            },
            label = { Text(stringResource(localesR.string.currency)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            enabled = enabled,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                selectedCurrency?.let {
                    onCurrencyClick(it)
                    currencySearchText = it.currencyCode
                    focusManager.clearFocus()
                }
            },
            modifier = Modifier.heightIn(max = dropDownHeight),
        ) {
            filteredCurrencies.forEach { selectionOption ->
                val currencyText = "${selectionOption.currencyCode} - ${selectionOption.displayName}"
                DropdownMenuItem(
                    text = { Text(currencyText) },
                    onClick = {
                        selectedCurrency = selectionOption
                        onCurrencyClick(selectionOption)
                        currencySearchText = selectionOption.currencyCode
                        expanded = false
                        focusManager.clearFocus()
                    },
                    leadingIcon = if (currency == selectionOption) {
                        {
                            Icon(
                                imageVector = CsIcons.Outlined.Check,
                                contentDescription = null,
                            )
                        }
                    } else null,
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
            if (filteredCurrencies.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(stringResource(localesR.string.currency_not_found)) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        expanded = false
                        focusManager.clearFocus()
                    },
                )
            }
        }
    }
}