package ru.resodostudios.cashsense.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Category
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Home
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Settings
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Autorenew
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Category
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Home
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Settings
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Wallet
import ru.resodostudios.cashsense.feature.category.dialog.navigation.CategoryDialogRoute
import ru.resodostudios.cashsense.feature.category.list.navigation.CategoriesBaseRoute
import ru.resodostudios.cashsense.feature.category.list.navigation.CategoriesRoute
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import ru.resodostudios.cashsense.feature.settings.navigation.SettingsBaseRoute
import ru.resodostudios.cashsense.feature.settings.navigation.SettingsRoute
import ru.resodostudios.cashsense.feature.subscription.dialog.navigation.SubscriptionDialogRoute
import ru.resodostudios.cashsense.feature.subscription.list.navigation.SubscriptionsBaseRoute
import ru.resodostudios.cashsense.feature.subscription.list.navigation.SubscriptionsRoute
import ru.resodostudios.cashsense.feature.transaction.dialog.navigation.TransactionDialogRoute
import ru.resodostudios.cashsense.feature.transfer.navigation.TransferDialogRoute
import ru.resodostudios.cashsense.feature.wallet.dialog.navigation.WalletDialogRoute
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
 * @param fabConfig An optional configuration for the Floating Action Button. If null, no FAB is shown. This typically includes the FAB's icon and content description.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    val routes: Set<KClass<*>>,
    val baseRoute: KClass<*>,
    val fabConfig: FabConfig? = null,
) {
    HOME(
        selectedIcon = CsIcons.Filled.Home,
        unselectedIcon = CsIcons.Outlined.Home,
        iconTextId = localesR.string.home_title,
        fabConfig = FabConfig(
            icon = CsIcons.Outlined.Wallet,
            title = localesR.string.new_wallet,
        ),
        routes = setOf(
            HomeRoute::class,
            WalletDialogRoute::class,
            TransferDialogRoute::class,
            TransactionDialogRoute::class,
        ),
        baseRoute = HomeListDetailRoute::class,
    ),
    CATEGORIES(
        selectedIcon = CsIcons.Filled.Category,
        unselectedIcon = CsIcons.Outlined.Category,
        iconTextId = localesR.string.categories_title,
        fabConfig = FabConfig(
            icon = CsIcons.Outlined.Category,
            title = localesR.string.new_category,
        ),
        routes = setOf(
            CategoriesRoute::class,
            CategoryDialogRoute::class,
        ),
        baseRoute = CategoriesBaseRoute::class,
    ),
    SUBSCRIPTIONS(
        selectedIcon = CsIcons.Outlined.Autorenew,
        unselectedIcon = CsIcons.Outlined.Autorenew,
        iconTextId = localesR.string.subscriptions_title,
        fabConfig = FabConfig(
            icon = CsIcons.Outlined.Autorenew,
            title = localesR.string.new_subscription,
        ),
        routes = setOf(
            SubscriptionsRoute::class,
            SubscriptionDialogRoute::class,
        ),
        baseRoute = SubscriptionsBaseRoute::class,
    ),
    SETTINGS(
        selectedIcon = CsIcons.Filled.Settings,
        unselectedIcon = CsIcons.Outlined.Settings,
        iconTextId = localesR.string.settings_title,
        routes = setOf(
            SettingsRoute::class,
        ),
        baseRoute = SettingsBaseRoute::class,
    )
}

data class FabConfig(
    val icon: ImageVector,
    @StringRes val title: Int,
)