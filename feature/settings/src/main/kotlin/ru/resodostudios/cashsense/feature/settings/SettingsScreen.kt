package ru.resodostudios.cashsense.feature.settings

import android.content.Context
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsSwitch
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Feedback
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.FolderZip
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.FormatPaint
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.HistoryEdu
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Info
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Language
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Palette
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Policy
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SettingsBackupRestore
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.UniversalCurrencyAlt
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.designsystem.theme.supportsDynamicTheming
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.model.data.Language
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.settings.SettingsUiState.Loading
import ru.resodostudios.cashsense.feature.settings.SettingsUiState.Success
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun SettingsScreen(
    onLicensesClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settingsState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    SettingsScreen(
        settingsState = settingsState,
        onLicensesClick = onLicensesClick,
        onDynamicColorPreferenceUpdate = viewModel::updateDynamicColorPreference,
        onDarkThemeConfigUpdate = viewModel::updateDarkThemeConfig,
        onCurrencyUpdate = viewModel::updateCurrency,
        onLanguageUpdate = viewModel::updateLanguage,
        onDataExport = viewModel::exportData,
        onDataImport = viewModel::importData,
    )
}

@Composable
private fun SettingsScreen(
    settingsState: SettingsUiState,
    onLicensesClick: () -> Unit,
    onDynamicColorPreferenceUpdate: (Boolean) -> Unit,
    onDarkThemeConfigUpdate: (DarkThemeConfig) -> Unit,
    onCurrencyUpdate: (Currency) -> Unit,
    onLanguageUpdate: (String) -> Unit,
    onDataExport: (Uri) -> Unit,
    onDataImport: (Uri, Boolean) -> Unit,
) {
    when (settingsState) {
        Loading -> LoadingState(Modifier.fillMaxSize())
        is Success -> {
            val context = LocalContext.current
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                general(
                    settings = settingsState.settings,
                    onCurrencyUpdate = onCurrencyUpdate,
                    onLanguageUpdate = onLanguageUpdate,
                )
                appearance(
                    settings = settingsState.settings,
                    onDynamicColorPreferenceUpdate = onDynamicColorPreferenceUpdate,
                    onDarkThemeConfigUpdate = onDarkThemeConfigUpdate,
                )
                backupAndRestore(
                    onDataExport = onDataExport,
                    onDataImport = onDataImport,
                )
                about(
                    context = context,
                    onLicensesClick = onLicensesClick,
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    topPadding: Dp = 32.dp,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier.padding(top = topPadding, bottom = 16.dp, start = 16.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.primary,
    )
}

@PreviewLightDark
@Composable
fun SettingsScreenPreview() {
    CsTheme {
        Surface {
            SettingsScreen(
                settingsState = Success(
                    settings = UserEditableSettings(
                        useDynamicColor = true,
                        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                        currency = getUsdCurrency(),
                        language = Language("en", "English"),
                        availableLanguages = emptyList(),
                    )
                ),
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

private fun LazyListScope.general(
    settings: UserEditableSettings,
    onCurrencyUpdate: (Currency) -> Unit,
    onLanguageUpdate: (String) -> Unit,
) {
    item {
        SectionTitle(
            text = stringResource(localesR.string.settings_general),
            topPadding = 10.dp,
        )
    }
    item {
        var showCurrencyDialog by rememberSaveable { mutableStateOf(false) }

        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.currency)) },
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.UniversalCurrencyAlt,
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
    }
    item {
        var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.language)) },
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.Language,
                    contentDescription = null,
                )
            },
            supportingContent = { Text(settings.language.name) },
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
}

private fun LazyListScope.appearance(
    settings: UserEditableSettings,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onDynamicColorPreferenceUpdate: (Boolean) -> Unit,
    onDarkThemeConfigUpdate: (DarkThemeConfig) -> Unit,
) {
    item { SectionTitle(stringResource(localesR.string.settings_appearance)) }
    item {
        val themeOptions = listOf(
            stringResource(localesR.string.theme_system_default),
            stringResource(localesR.string.theme_light),
            stringResource(localesR.string.theme_dark),
        )
        var showThemeDialog by rememberSaveable { mutableStateOf(false) }

        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.theme)) },
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.Palette,
                    contentDescription = null,
                )
            },
            supportingContent = { Text(themeOptions.elementAt(settings.darkThemeConfig.ordinal)) },
            onClick = { showThemeDialog = true },
        )

        if (showThemeDialog) {
            ThemeDialog(
                themeConfig = settings.darkThemeConfig,
                themeOptions = themeOptions,
                onDarkThemeConfigUpdate = onDarkThemeConfigUpdate,
                onDismiss = { showThemeDialog = false },
            )
        }
    }
    item {
        AnimatedVisibility(supportDynamicColor) {
            CsListItem(
                headlineContent = { Text(stringResource(localesR.string.dynamic_color)) },
                leadingContent = {
                    Icon(
                        imageVector = CsIcons.Outlined.FormatPaint,
                        contentDescription = null,
                    )
                },
                trailingContent = {
                    CsSwitch(
                        checked = settings.useDynamicColor,
                        onCheckedChange = onDynamicColorPreferenceUpdate,
                    )
                },
            )
        }
    }
}

private fun LazyListScope.backupAndRestore(
    onDataExport: (Uri) -> Unit,
    onDataImport: (Uri, Boolean) -> Unit,
) {
    item { SectionTitle(stringResource(localesR.string.backup_and_restore)) }
    item {
        val exportDbLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.CreateDocument("application/zip"),
            ) {
                it?.let { onDataExport(it) }
            }
        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.backup)) },
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.FolderZip,
                    contentDescription = null,
                )
            },
            supportingContent = { Text(stringResource(localesR.string.backup_description)) },
            onClick = { exportDbLauncher.launch("CASH_SENSE_BACKUP") },
        )
    }
    item {
        val importDbLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument(),
            ) {
                it?.let { onDataImport(it, true) }
            }
        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.restore)) },
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.SettingsBackupRestore,
                    contentDescription = null,
                )
            },
            supportingContent = { Text(stringResource(localesR.string.restore_description)) },
            onClick = { importDbLauncher.launch(arrayOf("application/zip")) },
        )
    }
}

private fun LazyListScope.about(
    context: Context,
    onLicensesClick: () -> Unit,
) {
    item { SectionTitle(stringResource(localesR.string.about)) }
    item {
        val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.feedback)) },
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.Feedback,
                    contentDescription = null,
                )
            },
            onClick = {
                launchCustomChromeTab(
                    context = context,
                    uri = FEEDBACK_URL.toUri(),
                    toolbarColor = backgroundColor,
                )
            },
        )
    }
    item {
        val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.privacy_policy)) },
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.Policy,
                    contentDescription = null,
                )
            },
            onClick = {
                launchCustomChromeTab(
                    context = context,
                    uri = PRIVACY_POLICY_URL.toUri(),
                    toolbarColor = backgroundColor,
                )
            },
        )
    }
    item {
        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.licenses)) },
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.HistoryEdu,
                    contentDescription = null,
                )
            },
            onClick = onLicensesClick,
        )
    }
    item {
        val packageInfo: PackageInfo? =
            context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo?.versionName ?: stringResource(localesR.string.none)
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            "(${packageInfo?.longVersionCode})"
        } else {
            ""
        }

        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.version)) },
            supportingContent = { Text("$versionName $versionCode") },
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.Info,
                    contentDescription = null,
                )
            },
        )
    }
}

private fun launchCustomChromeTab(
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