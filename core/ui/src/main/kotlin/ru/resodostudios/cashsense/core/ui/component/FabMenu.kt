package ru.resodostudios.cashsense.core.ui.component

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Autorenew
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Category
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Wallet
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FabMenu(
    visible: Boolean,
    onMenuItemClick: (FabMenuItem) -> Unit,
    modifier: Modifier = Modifier,
    toggleContainerSize: (Float) -> Dp = ToggleFloatingActionButtonDefaults.containerSize(),
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val items = FabMenuItem.entries
    val closeText = stringResource(localesR.string.close)
    val toggleMenuText = stringResource(localesR.string.toggle_menu)
    val expandedText = stringResource(localesR.string.expanded)
    val collapsedText = stringResource(localesR.string.collapsed)

    BackHandler(expanded) { expanded = false }

    FloatingActionButtonMenu(
        modifier = modifier,
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                modifier = Modifier
                    .semantics {
                        traversalIndex = -1f
                        stateDescription = if (expanded) expandedText else collapsedText
                        contentDescription = toggleMenuText
                    }
                    .animateFloatingActionButton(
                        visible = visible || expanded,
                        alignment = Alignment.BottomEnd,
                    )
                    .focusRequester(focusRequester),
                checked = expanded,
                onCheckedChange = { expanded = !expanded },
                containerSize = toggleContainerSize,
            ) {
                val imageVector by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) CsIcons.Outlined.Close else CsIcons.Outlined.Add
                    }
                }
                Icon(
                    painter = rememberVectorPainter(imageVector),
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress }),
                )
            }
        },
    ) {
        items.forEachIndexed { index, item ->
            FloatingActionButtonMenuItem(
                modifier = Modifier
                    .semantics {
                        isTraversalGroup = true
                        if (index == items.size - 1) {
                            customActions =
                                listOf(
                                    CustomAccessibilityAction(
                                        label = closeText,
                                        action = {
                                            expanded = false
                                            true
                                        },
                                    )
                                )
                        }
                    }
                    .then(
                        if (index == 0) {
                            Modifier.onKeyEvent {
                                if (
                                    it.type == KeyEventType.KeyDown &&
                                    (it.key == Key.DirectionUp ||
                                            (it.isShiftPressed && it.key == Key.Tab))
                                ) {
                                    focusRequester.requestFocus()
                                    return@onKeyEvent true
                                }
                                return@onKeyEvent false
                            }
                        } else {
                            Modifier
                        }
                    ),
                onClick = {
                    onMenuItemClick(item)
                    expanded = false
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                    )
                },
                text = {
                    Text(
                        text = stringResource(item.titleRes),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}

enum class FabMenuItem(
    val icon: ImageVector,
    @StringRes val titleRes: Int,
) {
    WALLET(
        icon = CsIcons.Outlined.Wallet,
        titleRes = localesR.string.new_wallet,
    ),
    CATEGORY(
        icon = CsIcons.Outlined.Category,
        titleRes = localesR.string.new_category,
    ),
    SUBSCRIPTION(
        icon = CsIcons.Outlined.Autorenew,
        titleRes = localesR.string.new_subscription,
    ),
}