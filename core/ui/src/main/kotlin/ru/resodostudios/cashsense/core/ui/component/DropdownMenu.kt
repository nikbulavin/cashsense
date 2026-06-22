package ru.resodostudios.cashsense.core.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.common.getValidCurrencies
import ru.resodostudios.cashsense.core.designsystem.component.CsOutlinedTextField
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.model.MenuWallet
import ru.resodostudios.cashsense.core.ui.util.formatAmount
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
        CsOutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            value = currencySearchText,
            singleLine = true,
            onValueChange = { newText ->
                currencySearchText = newText
                expanded = true
            },
            labelText = stringResource(localesR.string.currency),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
                )
            },
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
            shape = MenuDefaults.standaloneGroupShape,
            modifier = Modifier.heightIn(max = dropDownHeight),
            containerColor = MenuDefaults.groupStandardContainerColor,
        ) {
            filteredCurrencies.forEachIndexed { index, option ->
                DropdownMenuItem(
                    shapes = MenuDefaults.itemShape(index, filteredCurrencies.size),
                    text = {
                        Text(
                            text = "${option.currencyCode} - ${option.displayName}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = {
                        selectedCurrency = option
                        onCurrencyClick(option)
                        currencySearchText = option.currencyCode
                        expanded = false
                        focusManager.clearFocus()
                    },
                    selected = currency == option,
                    selectedLeadingIcon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Check,
                            contentDescription = null,
                        )
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
            if (filteredCurrencies.isEmpty()) {
                DropdownMenuItem(
                    shapes = MenuDefaults.itemShape(0, 1),
                    text = { Text(stringResource(localesR.string.currency_not_found)) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    selected = false,
                    onClick = {
                        expanded = false
                        focusManager.clearFocus()
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDropdownMenu(
    @StringRes title: Int,
    onWalletSelect: (MenuWallet) -> Unit,
    selectedWallet: MenuWallet,
    availableWallets: List<MenuWallet>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        CsOutlinedTextField(
            value = selectedWallet.title,
            onValueChange = {},
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            singleLine = true,
            labelText = stringResource(title),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            placeholderText = stringResource(localesR.string.choose_wallet),
            supportingText = selectedWallet.currency?.let {
                selectedWallet.currentBalance.formatAmount(it)
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MenuDefaults.standaloneGroupShape,
            containerColor = MenuDefaults.groupStandardContainerColor,
        ) {
            availableWallets.forEachIndexed { index, wallet ->
                val currentBalance = wallet.currency?.let { wallet.currentBalance.formatAmount(it) }
                DropdownMenuItem(
                    shapes = MenuDefaults.itemShape(index, availableWallets.size),
                    selected = selectedWallet == wallet,
                    selectedLeadingIcon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Check,
                            modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                            contentDescription = null,
                        )
                    },
                    text = {
                        Text(
                            text = wallet.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    supportingText = currentBalance?.let {
                        {
                            Text(
                                text = it,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },
                    onClick = {
                        onWalletSelect(wallet)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}