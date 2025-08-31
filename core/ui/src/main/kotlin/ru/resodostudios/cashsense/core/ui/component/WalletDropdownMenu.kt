package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Edit
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.MoreVert
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.locales.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDropdownMenu(
    onTransferClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.wrapContentSize(Alignment.TopStart),
    ) {
        CsIconButton(
            onClick = { expanded = true },
            icon = CsIcons.Outlined.MoreVert,
            contentDescription = stringResource(R.string.wallet_menu_icon_description),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.transfer)) },
                onClick = {
                    onTransferClick()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = CsIcons.Outlined.SendMoney,
                        contentDescription = null,
                    )
                },
            )
            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.edit)) },
                onClick = {
                    onEditClick()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = CsIcons.Outlined.Edit,
                        contentDescription = null,
                    )
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete)) },
                onClick = {
                    onDeleteClick()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = CsIcons.Outlined.Delete,
                        contentDescription = null,
                    )
                },
            )
        }
    }
}