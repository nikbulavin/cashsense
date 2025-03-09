package ru.resodostudios.cashsense.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
import ru.resodostudios.cashsense.core.data.repository.OfflineFirstCurrencyConversionRepository
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.data.repository.offline.OfflineCategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.offline.OfflineSubscriptionsRepository
import ru.resodostudios.cashsense.core.data.repository.offline.OfflineTransactionRepository
import ru.resodostudios.cashsense.core.data.repository.offline.OfflineUserDataRepository
import ru.resodostudios.cashsense.core.data.repository.offline.OfflineWalletsRepository
import ru.resodostudios.cashsense.core.data.util.AppLocaleManager
import ru.resodostudios.cashsense.core.data.util.AppLocaleManagerImpl
import ru.resodostudios.cashsense.core.data.util.InAppUpdateManager
import ru.resodostudios.cashsense.core.data.util.InAppUpdateManagerImpl
import ru.resodostudios.cashsense.core.data.util.ReminderScheduler
import ru.resodostudios.cashsense.core.data.util.ReminderSchedulerImpl
import ru.resodostudios.cashsense.core.data.util.TimeZoneBroadcastMonitor
import ru.resodostudios.cashsense.core.data.util.TimeZoneMonitor

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    internal abstract fun bindsCategoriesRepository(
        categoriesRepository: OfflineCategoriesRepository,
    ): CategoriesRepository

    @Binds
    internal abstract fun bindsCurrencyConversionRepository(
        currencyConversionRepository: OfflineFirstCurrencyConversionRepository,
    ): CurrencyConversionRepository

    @Binds
    internal abstract fun bindsTransactionsRepository(
        transactionsRepository: OfflineTransactionRepository,
    ): TransactionsRepository

    @Binds
    internal abstract fun bindsWalletsRepository(
        walletsRepository: OfflineWalletsRepository,
    ): WalletsRepository

    @Binds
    internal abstract fun bindsSubscriptionsRepository(
        subscriptionsRepository: OfflineSubscriptionsRepository,
    ): SubscriptionsRepository

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsTimeZoneMonitor(
        timeZoneMonitor: TimeZoneBroadcastMonitor,
    ): TimeZoneMonitor

    @Binds
    internal abstract fun bindsNotificationAlarmScheduler(
        reminderScheduler: ReminderSchedulerImpl,
    ): ReminderScheduler

    @Binds
    internal abstract fun bindsInAppUpdateManager(
        inAppUpdateManager: InAppUpdateManagerImpl,
    ): InAppUpdateManager

    @Binds
    internal abstract fun bindsAppLocaleManager(
        appLocaleManager: AppLocaleManagerImpl,
    ): AppLocaleManager
}