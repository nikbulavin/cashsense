package ru.resodostudios.cashsense.feature.settings.impl

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsListItemEmphasized
import ru.resodostudios.cashsense.core.designsystem.component.CsSwitch
import ru.resodostudios.cashsense.core.designsystem.component.CsToggableListItem
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.DarkMode
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Feedback
import ru.resodostudios.cashsense.core.designsystem.icon.filled.FolderZip
import ru.resodostudios.cashsense.core.designsystem.icon.filled.FormatPaint
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Gavel
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Info
import ru.resodostudios.cashsense.core.designsystem.icon.filled.LightMode
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Palette
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Policy
import ru.resodostudios.cashsense.core.designsystem.icon.filled.UniversalCurrencyAlt
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Android
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Language
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SettingsBackupRestore
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.designsystem.theme.supportsDynamicTheming
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.model.data.Language
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.SectionTitle
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.formatDate
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.time.format.FormatStyle
import java.util.Currency
import kotlin.time.Clock
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun SettingsScreen(
    onBackClick: () -> Unit,
    onLicensesClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settingsState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    SettingsScreen(
        settingsState = settingsState,
        onBackClick = onBackClick,
        onLicensesClick = onLicensesClick,
        onDynamicColorPreferenceUpdate = viewModel::updateDynamicColorPreference,
        onDarkThemeConfigUpdate = viewModel::updateDarkThemeConfig,
        onCurrencyUpdate = viewModel::updateCurrency,
        onLanguageUpdate = viewModel::updateLanguage,
        onDataExport = viewModel::exportData,
        onDataImport = viewModel::importData,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    settingsState: SettingsUiState,
    onBackClick: () -> Unit,
    onLicensesClick: () -> Unit,
    onDynamicColorPreferenceUpdate: (Boolean) -> Unit,
    onDarkThemeConfigUpdate: (DarkThemeConfig) -> Unit,
    onCurrencyUpdate: (Currency) -> Unit,
    onLanguageUpdate: (String) -> Unit,
    onDataExport: (Uri) -> Unit,
    onDataImport: (Uri, Boolean) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        when (settingsState) {
            SettingsUiState.Loading -> {
                LoadingState(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                )
            }

            is SettingsUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            bottom = 16.dp + innerPadding.calculateBottomPadding(),
                            start = 16.dp,
                            end = 16.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    General(
                        settings = settingsState.settings,
                        onCurrencyUpdate = onCurrencyUpdate,
                        onLanguageUpdate = onLanguageUpdate,
                    )
                    Appearance(
                        settings = settingsState.settings,
                        onDynamicColorPreferenceUpdate = onDynamicColorPreferenceUpdate,
                        onDarkThemeConfigUpdate = onDarkThemeConfigUpdate,
                    )
                    BackupAndRestore(
                        onDataExport = onDataExport,
                        onDataImport = onDataImport,
                    )
                    About(
                        onLicensesClick = onLicensesClick,
                    )
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "Settings")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    LargeFlexibleTopAppBar(
        title = { Text(stringResource(localesR.string.settings_title)) },
        navigationIcon = {
            CsIconButton(
                onClick = onBackClick,
                icon = CsIcons.Outlined.ArrowBack,
                contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                tooltipPosition = TooltipAnchorPosition.Right,
            )
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@PreviewLightDark
@Composable
fun SettingsScreenPreview() {
    CsTheme {
        Surface {
            SettingsScreen(
                settingsState = SettingsUiState.Success(
                    settings = UserEditableSettings(
                        useDynamicColor = true,
                        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                        currency = getUsdCurrency(),
                        language = Language.ENGLISH,
                        availableLanguages = emptyList(),
                    ),
                ),
                onBackClick = {},
                onLicensesClick = {},
                onDynamicColorPreferenceUpdate = {},
                onDarkThemeConfigUpdate = {},
                onCurrencyUpdate = {},
                onLanguageUpdate = {},
                onDataExport = {},
                onDataImport = { _, _ -> },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun General(
    settings: UserEditableSettings,
    onCurrencyUpdate: (Currency) -> Unit,
    onLanguageUpdate: (String) -> Unit,
) {
    SectionTitle(localesR.string.settings_general)
    
    var showCurrencyDialog by rememberSaveable { mutableStateOf(false) }

    CsListItemEmphasized(
        shapes = ListItemDefaults.segmentedShapes(0, 2),
        content = { Text(stringResource(localesR.string.currency)) },
        leadingContent = {
            Icon(
                imageVector = CsIcons.Filled.UniversalCurrencyAlt,
                contentDescription = null,
            )
        },
        supportingContent = { Text(settings.currency.currencyCode) },
        onClick = { showCurrencyDialog = true },
    )

    if (showCurrencyDialog) {
        CurrencyDialog(
            currency = settings.currency,
            onDismiss = { showCurrencyDialog = false },
            onCurrencyClick = onCurrencyUpdate,
        )
    }

    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    CsListItemEmphasized(
        shapes = ListItemDefaults.segmentedShapes(1, 2),
        content = { Text(stringResource(localesR.string.language)) },
        leadingContent = {
            Icon(
                imageVector = CsIcons.Outlined.Language,
                contentDescription = null,
            )
        },
        supportingContent = { Text(settings.language.displayName) },
        onClick = { showLanguageDialog = true },
    )

    if (showLanguageDialog) {
        LanguageDialog(
            language = settings.language.code,
            availableLanguages = settings.availableLanguages,
            onLanguageClick = onLanguageUpdate,
            onDismiss = { showLanguageDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Appearance(
    settings: UserEditableSettings,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onDynamicColorPreferenceUpdate: (Boolean) -> Unit,
    onDarkThemeConfigUpdate: (DarkThemeConfig) -> Unit,
) {
    SectionTitle(localesR.string.settings_appearance)

    var shouldShowThemeDialog by rememberSaveable { mutableStateOf(false) }
    val themeOptions = listOf(
        stringResource(localesR.string.theme_system_default) to CsIcons.Outlined.Android,
        stringResource(localesR.string.theme_light) to CsIcons.Filled.LightMode,
        stringResource(localesR.string.theme_dark) to CsIcons.Filled.DarkMode,
    )
    
    CsListItemEmphasized(
        onClick = { shouldShowThemeDialog = true },
        shapes = if (supportDynamicColor) {
            ListItemDefaults.segmentedShapes(0, 2)
        } else {
            ListItemDefaults.shapes(shape = RoundedCornerShape(16.dp))
        },
        content = { Text(stringResource(localesR.string.theme)) },
        leadingContent = {
            Icon(
                imageVector = CsIcons.Filled.Palette,
                contentDescription = null,
            )
        },
        supportingContent = {
            Text(
                text = themeOptions[settings.darkThemeConfig.ordinal].first,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
    if (shouldShowThemeDialog) {
        ThemeDialog(
            themeConfig = settings.darkThemeConfig,
            themeOptions = themeOptions,
            onThemeConfigUpdate = onDarkThemeConfigUpdate,
            onDismiss = { shouldShowThemeDialog = false },
        )
    }

    if (supportDynamicColor) {
        CsToggableListItem(
            shapes = ListItemDefaults.segmentedShapes(1, 2),
            content = { Text(stringResource(localesR.string.dynamic_color)) },
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Filled.FormatPaint,
                    contentDescription = null,
                )
            },
            trailingContent = {
                CsSwitch(
                    checked = settings.useDynamicColor,
                    onCheckedChange = null,
                )
            },
            checked = settings.useDynamicColor,
            onCheckedChange = onDynamicColorPreferenceUpdate,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BackupAndRestore(
    onDataExport: (Uri) -> Unit,
    onDataImport: (Uri, Boolean) -> Unit,
) {
    SectionTitle(localesR.string.backup_and_restore)
    
    val exportDbLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip"),
    ) {
        it?.let { onDataExport(it) }
    }
    val date = Clock.System.now().formatDate(formatStyle = FormatStyle.SHORT)
    val fileName = "CASH_SENSE_BACKUP_${date.filter { it.isDigit() }}"
    CsListItemEmphasized(
        shapes = ListItemDefaults.segmentedShapes(0, 2),
        content = { Text(stringResource(localesR.string.backup)) },
        leadingContent = {
            Icon(
                imageVector = CsIcons.Filled.FolderZip,
                contentDescription = null,
            )
        },
        supportingContent = {
            Text(
                text = stringResource(localesR.string.backup_description),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        onClick = { exportDbLauncher.launch(fileName) },
    )

    val importDbLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) {
        it?.let { onDataImport(it, true) }
    }
    CsListItemEmphasized(
        shapes = ListItemDefaults.segmentedShapes(1, 2),
        content = { Text(stringResource(localesR.string.restore)) },
        leadingContent = {
            Icon(
                imageVector = CsIcons.Outlined.SettingsBackupRestore,
                contentDescription = null,
            )
        },
        supportingContent = {
            Text(
                text = stringResource(localesR.string.restore_description),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        onClick = { importDbLauncher.launch(arrayOf("application/zip")) },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun About(
    onLicensesClick: () -> Unit,
) {
    SectionTitle(localesR.string.about)
    
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    CsListItemEmphasized(
        shapes = ListItemDefaults.segmentedShapes(0, 4),
        content = { Text(stringResource(localesR.string.feedback)) },
        leadingContent = {
            Icon(
                imageVector = CsIcons.Filled.Feedback,
                contentDescription = null,
            )
        },
        onClick = {
            launchCustomTab(
                context = context,
                uri = FEEDBACK_URL.toUri(),
                toolbarColor = backgroundColor,
            )
        },
    )

    CsListItemEmphasized(
        shapes = ListItemDefaults.segmentedShapes(1, 4),
        content = { Text(stringResource(localesR.string.privacy_policy)) },
        leadingContent = {
            Icon(
                imageVector = CsIcons.Filled.Policy,
                contentDescription = null,
            )
        },
        onClick = {
            launchCustomTab(
                context = context,
                uri = PRIVACY_POLICY_URL.toUri(),
                toolbarColor = backgroundColor,
            )
        },
    )

    CsListItemEmphasized(
        shapes = ListItemDefaults.segmentedShapes(2, 4),
        content = { Text(stringResource(localesR.string.licenses)) },
        leadingContent = {
            Icon(
                imageVector = CsIcons.Filled.Gavel,
                contentDescription = null,
            )
        },
        onClick = onLicensesClick,
    )

    val packageInfo = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }.getOrNull()
    val versionName = packageInfo?.versionName ?: "?.?.?"
    val versionCode = "(${packageInfo?.longVersionCode})"

    CsListItemEmphasized(
        onClick = {},
        shapes = ListItemDefaults.segmentedShapes(3, 4),
        content = { Text(stringResource(localesR.string.version)) },
        supportingContent = { Text("$versionName $versionCode") },
        leadingContent = {
            Icon(
                imageVector = CsIcons.Filled.Info,
                contentDescription = null,
            )
        },
    )
}

private fun launchCustomTab(
    context: Context,
    uri: Uri,
    @ColorInt toolbarColor: Int,
) {
    val customTabBarColor = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor)
        .build()
    val customTabsIntent = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(customTabBarColor)
        .build()
    customTabsIntent.launchUrl(context, uri)
}

private const val FEEDBACK_URL =
    "https://trusted-cowl-779.notion.site/14066ebc684d8010b4dbfd9e36d8cb1e?pvs=105"
private const val PRIVACY_POLICY_URL =
    "https://trusted-cowl-779.notion.site/Privacy-Policy-65accc6cf3714f289392ae1ffee96bae?pvs=4"
