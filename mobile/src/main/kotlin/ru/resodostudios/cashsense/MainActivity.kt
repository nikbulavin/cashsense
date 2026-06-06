package ru.resodostudios.cashsense

import android.app.UiModeManager
import android.os.Build
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
import androidx.core.content.getSystemService
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.tracing.trace
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.MainActivityUiState.Loading
import ru.resodostudios.cashsense.MainActivityUiState.Success
import ru.resodostudios.cashsense.core.analytics.AnalyticsHelper
import ru.resodostudios.cashsense.core.analytics.LocalAnalyticsHelper
import ru.resodostudios.cashsense.core.data.util.InAppReviewManager
import ru.resodostudios.cashsense.core.data.util.InAppUpdateManager
import ru.resodostudios.cashsense.core.data.util.PermissionManager
import ru.resodostudios.cashsense.core.data.util.TimeZoneMonitor
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
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
            combine(
                isSystemInDarkTheme(),
                viewModel.uiState,
            ) { systemDark, uiState ->
                if (uiState is Success) {
                    updateApplicationNightMode(uiState.userData.darkThemeConfig)
                }
                ThemeSettings(
                    darkTheme = uiState.shouldUseDarkTheme(systemDark),
                    dynamicTheme = uiState.shouldUseDynamicTheming,
                )
            }
                .onEach { themeSettings = it }
                .map { it.darkTheme }
                .distinctUntilChanged()
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
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

        lifecycleScope.launch {
            runCatching {
                inAppReviewManager.openReviewDialog(this@MainActivity)
            }
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
                navigationState = rememberNavigationState(
                    syntheticBackStack,
                    TOP_LEVEL_NAV_ITEMS.keys,
                ),
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

    private fun updateApplicationNightMode(darkThemeConfig: DarkThemeConfig) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val uiModeManager: UiModeManager = checkNotNull(getSystemService())
            uiModeManager.setApplicationNightMode(
                when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM -> UiModeManager.MODE_NIGHT_AUTO
                    DarkThemeConfig.LIGHT -> UiModeManager.MODE_NIGHT_NO
                    DarkThemeConfig.DARK -> UiModeManager.MODE_NIGHT_YES
                },
            )
        }
    }
}

data class ThemeSettings(
    val darkTheme: Boolean,
    val dynamicTheme: Boolean,
)