package ru.resodostudios.cashsense.core.data.repository.offline

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.database.util.DatabaseTransferManager
import ru.resodostudios.cashsense.core.datastore.CsPreferencesDataSource
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.model.data.UserData
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager
import javax.inject.Inject

internal class OfflineUserDataRepository @Inject constructor(
    private val csPreferencesDataSource: CsPreferencesDataSource,
    private val shortcutManager: ShortcutManager,
    private val databaseTransferManager: DatabaseTransferManager,
) : UserDataRepository {

    override val userData: Flow<UserData> = csPreferencesDataSource.userData

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) =
        csPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) =
        csPreferencesDataSource.setDynamicColorPreference(useDynamicColor)

    override suspend fun setPrimaryWallet(id: String, isPrimary: Boolean) {
        if (isPrimary) {
            csPreferencesDataSource.setPrimaryWalletId(id)
            shortcutManager.addTransactionShortcut(id)
        } else if (csPreferencesDataSource.userData.first().primaryWalletId == id) {
            csPreferencesDataSource.setPrimaryWalletId("")
            shortcutManager.removeShortcuts()
        }
    }

    override suspend fun setCurrency(currency: String) =
        csPreferencesDataSource.setCurrency(currency)

    override fun exportData(backupFileUri: Uri) =
        databaseTransferManager.export(backupFileUri)

    override fun importData(backupFileUri: Uri, restart: Boolean) =
        databaseTransferManager.import(backupFileUri, restart)

    override suspend fun setTotalBalancePreference(shouldShowTotalBalance: Boolean) {
        csPreferencesDataSource.setTotalBalancePreference(shouldShowTotalBalance)
    }
}