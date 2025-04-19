package ru.resodostudios.cashsense.core.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.model.data.UserData

interface UserDataRepository {

    val userData: Flow<UserData>

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)

    suspend fun setPrimaryWallet(id: String, isPrimary: Boolean)

    suspend fun setCurrency(currency: String)

    fun exportData(backupFileUri: Uri)

    fun importData(backupFileUri: Uri, restart: Boolean = true)
}