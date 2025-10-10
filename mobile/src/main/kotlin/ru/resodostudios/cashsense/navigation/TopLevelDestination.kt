package ru.resodostudios.cashsense.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Category
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Home
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Settings
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
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
 * Type for the top-level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled nested navigation.
 *
 * @param selectedIcon The icon to show when the destination is selected.
 * @param unselectedIcon The icon to show when the destination is not selected.
 * @param iconTextId The string resource that uniquely identifies the icon content description.
 * @param routes The set of routes that are part of this destination.
 * @param baseRoute The base route for the destination, used for nested navigation.
 * @param fabIcon The icon for the floating action button, if any.
 * @param fabTitle The string resource for the floating action button's content description.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    val routes: Set<KClass<*>>,
    val baseRoute: KClass<*>,
    val fabIcon: ImageVector? = null,
    @StringRes val fabTitle: Int? = null,
) {
    HOME(
        selectedIcon = CsIcons.Filled.Home,
        unselectedIcon = CsIcons.Outlined.Home,
        iconTextId = localesR.string.home_title,
        fabIcon = CsIcons.Outlined.Wallet,
        fabTitle = localesR.string.new_wallet,
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
        fabIcon = CsIcons.Outlined.Add,
        fabTitle = localesR.string.new_category,
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
        fabIcon = CsIcons.Outlined.Add,
        fabTitle = localesR.string.new_subscription,
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