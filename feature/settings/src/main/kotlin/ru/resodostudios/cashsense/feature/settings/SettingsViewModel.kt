package ru.resodostudios.cashsense.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.util.AppLocaleManager
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.model.data.Language
import java.util.Currency
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val appLocaleManager: AppLocaleManager,
) : ViewModel() {

    private val availableLanguages = listOf(
        Language("en", "English"),
        Language("ru", "Русский"),
        Language("ar", "العربية"),
        Language("de", "Deutsch"),
        Language("es", "Español"),
        Language("fr", "Français"),
        Language("hi", "हिंदी"),
        Language("it", "Italiano"),
        Language("ja", "日本語"),
        Language("ko", "한국어"),
        Language("pl", "Polski"),
        Language("ta", "தமிழ்"),
        Language("zh", "简体中文"),
    )

    val settingsUiState: StateFlow<SettingsUiState> = combine(
        userDataRepository.userData,
        appLocaleManager.currentLocale,
    ) { userData, currentLocale ->
        val language = availableLanguages
            .find { it.code == currentLocale } ?: availableLanguages.first()
        SettingsUiState.Success(
            settings = UserEditableSettings(
                useDynamicColor = userData.useDynamicColor,
                darkThemeConfig = userData.darkThemeConfig,
                currency = Currency.getInstance(userData.currency),
                language = language,
                availableLanguages = availableLanguages,
                shouldShowTotalBalance = userData.shouldShowTotalBalance,
            )
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5_000),
            initialValue = SettingsUiState.Loading,
        )

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userDataRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }

    fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        viewModelScope.launch {
            userDataRepository.setDynamicColorPreference(useDynamicColor)
        }
    }

    fun updateCurrency(currency: Currency) {
        viewModelScope.launch {
            userDataRepository.setCurrency(currency.currencyCode)
        }
    }

    fun updateLanguage(language: String) {
        appLocaleManager.updateLocale(language)
    }

    fun exportData(backupFileUri: Uri) {
        userDataRepository.exportData(backupFileUri)
    }

    fun importData(backupFileUri: Uri, restart: Boolean = true) {
        userDataRepository.importData(backupFileUri, restart)
    }

    fun updateTotalBalanceVisibility(shouldShowTotalBalance: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTotalBalancePreference(shouldShowTotalBalance)
        }
    }
}

data class UserEditableSettings(
    val useDynamicColor: Boolean,
    val darkThemeConfig: DarkThemeConfig,
    val currency: Currency,
    val language: Language,
    val availableLanguages: List<Language>,
    val shouldShowTotalBalance: Boolean,
)

sealed interface SettingsUiState {

    data object Loading : SettingsUiState

    data class Success(val settings: UserEditableSettings) : SettingsUiState
}