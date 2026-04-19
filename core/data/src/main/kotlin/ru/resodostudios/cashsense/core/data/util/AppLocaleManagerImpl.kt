package ru.resodostudios.cashsense.core.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.tracing.trace
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import ru.resodostudios.cashsense.core.network.CsDispatchers.IO
import ru.resodostudios.cashsense.core.network.Dispatcher
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
internal class AppLocaleManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationScope appScope: CoroutineScope,
) : AppLocaleManager {

    override val currentLanguageTag: SharedFlow<String> = callbackFlow {
        trySend(getCurrentLanguage())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != Intent.ACTION_LOCALE_CHANGED) return
                trySend(getCurrentLanguage())
            }
        }

        trace("AppLocaleBroadcastReceiver.register") {
            context.registerReceiver(receiver, IntentFilter(Intent.ACTION_LOCALE_CHANGED))
        }

        trySend(getCurrentLanguage())

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
        .distinctUntilChanged()
        .conflate()
        .flowOn(ioDispatcher)
        .shareIn(
            scope = appScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            replay = 1,
        )

    override fun setApplicationLocale(languageTag: String) {
        runCatching {
            val localeList = if (languageTag.isEmpty()) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(languageTag)
            }
            AppCompatDelegate.setApplicationLocales(localeList)
        }.onFailure { exception ->
            exception.printStackTrace()
        }
    }

    private fun getCurrentLanguage(): String {
        return AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag() ?: ""
    }
}