package ru.resodostudios.cashsense.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Category
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Home
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Autorenew
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Category
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Home
import ru.resodostudios.cashsense.feature.category.list.api.CategoriesNavKey
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.cashsense.feature.subscription.list.api.SubscriptionsNavKey
import ru.resodostudios.cashsense.core.locales.R as localesR

data class TopLevelNavItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
)

val HOME = TopLevelNavItem(
    selectedIcon = CsIcons.Filled.Home,
    unselectedIcon = CsIcons.Outlined.Home,
    iconTextId = localesR.string.home_title,
)

val CATEGORIES = TopLevelNavItem(
    selectedIcon = CsIcons.Filled.Category,
    unselectedIcon = CsIcons.Outlined.Category,
    iconTextId = localesR.string.categories_title,
)

val SUBSCRIPTIONS = TopLevelNavItem(
    selectedIcon = CsIcons.Outlined.Autorenew,
    unselectedIcon = CsIcons.Outlined.Autorenew,
    iconTextId = localesR.string.subscriptions_title,
)

val TOP_LEVEL_NAV_ITEMS = mapOf(
    HomeNavKey() to HOME,
    CategoriesNavKey to CATEGORIES,
    SubscriptionsNavKey to SUBSCRIPTIONS,
)
