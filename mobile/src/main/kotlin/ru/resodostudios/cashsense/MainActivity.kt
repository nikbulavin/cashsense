package ru.resodostudios.cashsense

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.tracing.trace
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.MainActivityUiState.Loading
import ru.resodostudios.cashsense.core.analytics.AnalyticsHelper
import ru.resodostudios.cashsense.core.analytics.LocalAnalyticsHelper
import ru.resodostudios.cashsense.core.data.util.InAppReviewManager
import ru.resodostudios.cashsense.core.data.util.InAppUpdateManager
import ru.resodostudios.cashsense.core.data.util.PermissionManager
import ru.resodostudios.cashsense.core.data.util.TimeZoneMonitor
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager
import ru.resodostudios.cashsense.core.ui.LocalTimeZone
import ru.resodostudios.cashsense.navigation.TOP_LEVEL_NAV_ITEMS
import ru.resodostudios.cashsense.ui.CsApp
import ru.resodostudios.cashsense.ui.rememberCsAppState
import ru.resodostudios.cashsense.util.buildBackStack
import ru.resodostudios.cashsense.util.isSystemInDarkTheme
import ru.resodostudios.cashsense.util.toKey
import ru.resodostudios.core.navigation.rememberNavigationState
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    @Inject
    lateinit var inAppUpdateManager: InAppUpdateManager

    @Inject
    lateinit var inAppReviewManager: InAppReviewManager

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var shortcutManager: ShortcutManager

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var themeSettings by mutableStateOf(
            ThemeSettings(
                darkTheme = resources.configuration.isSystemInDarkTheme,
                dynamicTheme = Loading.shouldUseDynamicTheming,
            ),
        )

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(),
                    viewModel.uiState,
                ) { systemDark, uiState ->
                    ThemeSettings(
                        darkTheme = uiState.shouldUseDarkTheme(systemDark),
                        dynamicTheme = uiState.shouldUseDynamicTheming,
                    )
                }
                    .onEach { themeSettings = it }
                    .map { it.darkTheme }
                    .distinctUntilChanged()
                    .collect { darkTheme ->
                        trace("csEdgeToEdge") {
                            enableEdgeToEdge(
                                statusBarStyle = SystemBarStyle.auto(
                                    lightScrim = Color.Transparent.toArgb(),
                                    darkScrim = Color.Transparent.toArgb(),
                                ) { darkTheme },
                                navigationBarStyle = SystemBarStyle.auto(
                                    lightScrim = Color.Transparent.toArgb(),
                                    darkScrim = Color.Transparent.toArgb(),
                                ) { darkTheme },
                            )
                        }
                    }
            }
            inAppReviewManager.openReviewDialog(this@MainActivity)
        }
        shortcutManager.syncTransactionShortcut()

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }

        val startKey = intent.data.toKey()
        val syntheticBackStack = buildBackStack(
            startKey = startKey,
        )

        setContent {
            val appState = rememberCsAppState(
                timeZoneMonitor = timeZoneMonitor,
                inAppUpdateManager = inAppUpdateManager,
                permissionManager = permissionManager,
                navigationState = rememberNavigationState(syntheticBackStack, TOP_LEVEL_NAV_ITEMS.keys),
            )

            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper,
                LocalTimeZone provides currentTimeZone,
            ) {
                CsTheme(
                    darkTheme = themeSettings.darkTheme,
                    dynamicTheme = themeSettings.dynamicTheme,
                ) {
                    CsApp(appState)
                }
            }
        }
    }
}

data class ThemeSettings(
    val darkTheme: Boolean,
    val dynamicTheme: Boolean,
)