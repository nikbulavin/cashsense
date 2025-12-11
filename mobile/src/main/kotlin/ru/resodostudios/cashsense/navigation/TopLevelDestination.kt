package ru.resodostudios.cashsense.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Home
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Autorenew
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Home
import ru.resodostudios.cashsense.feature.home.impl.navigation.HomeRoute
import ru.resodostudios.cashsense.feature.subscription.list.impl.navigation.SubscriptionsRoute
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.navigation.TransactionDialogRoute
import ru.resodostudios.cashsense.feature.transfer.impl.navigation.TransferDialogRoute
import ru.resodostudios.cashsense.ui.home2pane.HomeListDetailRoute
import kotlin.reflect.KClass
import ru.resodostudios.cashsense.core.locales.R as localesR

/**
 * Represents the primary navigation destinations in the application.
 *
 * Each destination is defined by its icons (for selected and unselected states),
 * a textual label, a set of associated navigation routes, a base route for nested navigation,
 * and an optional configuration for a Floating Action Button (FAB).
 *
 * @param selectedIcon The icon displayed when this destination is currently selected.
 * @param unselectedIcon The icon displayed when this destination is not selected.
 * @param iconTextId The string resource ID for the label of the destination, used for accessibility and display.
 * @param routes A set of all `KClass` routes that belong to this top-level destination. This is used to determine which destination is currently active.
 * @param baseRoute The primary or starting route `KClass` for the destination's navigation graph.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    val routes: Set<KClass<*>>,
    val baseRoute: KClass<*>,
) {
    HOME(
        selectedIcon = CsIcons.Filled.Home,
        unselectedIcon = CsIcons.Outlined.Home,
        iconTextId = localesR.string.home_title,
        routes = setOf(
            HomeRoute::class,
            TransferDialogRoute::class,
            TransactionDialogRoute::class,
        ),
        baseRoute = HomeListDetailRoute::class,
    ),
    SUBSCRIPTIONS(
        selectedIcon = CsIcons.Outlined.Autorenew,
        unselectedIcon = CsIcons.Outlined.Autorenew,
        iconTextId = localesR.string.subscriptions_title,
        routes = setOf(
            SubscriptionsRoute::class,
        ),
        baseRoute = SubscriptionsRoute::class,
    ),
}