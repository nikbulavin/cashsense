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
import ru.resodostudios.cashsense.feature.category.list.navigation.CategoriesBaseRoute
import ru.resodostudios.cashsense.feature.category.list.navigation.CategoriesRoute
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import ru.resodostudios.cashsense.feature.settings.navigation.SettingsBaseRoute
import ru.resodostudios.cashsense.feature.settings.navigation.SettingsRoute
import ru.resodostudios.cashsense.feature.subscription.list.navigation.SubscriptionsBaseRoute
import ru.resodostudios.cashsense.feature.subscription.list.navigation.SubscriptionsRoute
import ru.resodostudios.cashsense.ui.home2pane.HomeListDetailRoute
import kotlin.reflect.KClass
import ru.resodostudios.cashsense.core.locales.R as localesR

/**
 * Enum class representing the top-level destinations in the application.
 *
 * Each destination corresponds to a screen accessible from the main navigation bar.
 * It holds information about the destination's icons, titles, routes, and associated FAB (Floating Action Button) data.
 *
 * @property selectedIcon The [ImageVector] to display for the destination when it is selected in the navigation bar.
 * @property unselectedIcon The [ImageVector] to display for the destination when it is not selected in the navigation bar.
 * @property iconTextId The [StringRes] resource ID for the content description of the icon.
 * @property titleTextId The [StringRes] resource ID for the title of the destination.
 * @property route The [KClass] representing the composable route associated with the destination.
 * @property baseRoute The [KClass] representing the base route for the destination, for nested navigation. Defaults to [route] if not provided.
 * @property fabIcon The [ImageVector] to display on the FAB when this destination is active. Null if no FAB is needed.
 * @property fabTitle The [StringRes] resource ID for the content description or title of the FAB when this destination is active. Null if no FAB is needed.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
    val fabIcon: ImageVector? = null,
    @StringRes val fabTitle: Int? = null,
) {
    HOME(
        selectedIcon = CsIcons.Filled.Home,
        unselectedIcon = CsIcons.Outlined.Home,
        iconTextId = localesR.string.home_title,
        titleTextId = localesR.string.home_title,
        fabIcon = CsIcons.Outlined.Wallet,
        fabTitle = localesR.string.new_wallet,
        route = HomeRoute::class,
        baseRoute = HomeListDetailRoute::class,
    ),
    CATEGORIES(
        selectedIcon = CsIcons.Filled.Category,
        unselectedIcon = CsIcons.Outlined.Category,
        iconTextId = localesR.string.categories_title,
        titleTextId = localesR.string.categories_title,
        fabIcon = CsIcons.Outlined.Add,
        fabTitle = localesR.string.new_category,
        route = CategoriesRoute::class,
        baseRoute = CategoriesBaseRoute::class,
    ),
    SUBSCRIPTIONS(
        selectedIcon = CsIcons.Outlined.Autorenew,
        unselectedIcon = CsIcons.Outlined.Autorenew,
        iconTextId = localesR.string.subscriptions_title,
        titleTextId = localesR.string.subscriptions_title,
        fabIcon = CsIcons.Outlined.Add,
        fabTitle = localesR.string.new_subscription,
        route = SubscriptionsRoute::class,
        baseRoute = SubscriptionsBaseRoute::class,
    ),
    SETTINGS(
        selectedIcon = CsIcons.Filled.Settings,
        unselectedIcon = CsIcons.Outlined.Settings,
        iconTextId = localesR.string.settings_title,
        titleTextId = localesR.string.settings_title,
        route = SettingsRoute::class,
        baseRoute = SettingsBaseRoute::class,
    )
}