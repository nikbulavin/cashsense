package ru.resodostudios.cashsense.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.data.repository.impl.OfflineCategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.impl.OfflineFirstCurrencyConversionRepository
import ru.resodostudios.cashsense.core.data.repository.impl.OfflineSubscriptionsRepository
import ru.resodostudios.cashsense.core.data.repository.impl.OfflineTransactionRepository
import ru.resodostudios.cashsense.core.data.repository.impl.OfflineUserDataRepository
import ru.resodostudios.cashsense.core.data.repository.impl.OfflineWalletsRepository
import ru.resodostudios.cashsense.core.data.util.AppLocaleManager
import ru.resodostudios.cashsense.core.data.util.AppLocaleManagerImpl
import ru.resodostudios.cashsense.core.data.util.InAppReviewManager
import ru.resodostudios.cashsense.core.data.util.InAppReviewManagerImpl
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
        impl: OfflineCategoriesRepository,
    ): CategoriesRepository

    @Binds
    internal abstract fun bindsCurrencyConversionRepository(
        impl: OfflineFirstCurrencyConversionRepository,
    ): CurrencyConversionRepository

    @Binds
    internal abstract fun bindsTransactionsRepository(
        impl: OfflineTransactionRepository,
    ): TransactionsRepository

    @Binds
    internal abstract fun bindsWalletsRepository(
        impl: OfflineWalletsRepository,
    ): WalletsRepository

    @Binds
    internal abstract fun bindsSubscriptionsRepository(
        impl: OfflineSubscriptionsRepository,
    ): SubscriptionsRepository

    @Binds
    internal abstract fun bindsUserDataRepository(
        impl: OfflineUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsTimeZoneMonitor(
        impl: TimeZoneBroadcastMonitor,
    ): TimeZoneMonitor

    @Binds
    internal abstract fun bindsNotificationAlarmScheduler(
        impl: ReminderSchedulerImpl,
    ): ReminderScheduler

    @Binds
    internal abstract fun bindsInAppUpdateManager(
        impl: InAppUpdateManagerImpl,
    ): InAppUpdateManager

    @Binds
    internal abstract fun bindsInAppReviewManager(
        impl: InAppReviewManagerImpl,
    ): InAppReviewManager

    @Binds
    internal abstract fun bindsAppLocaleManager(
        impl: AppLocaleManagerImpl,
    ): AppLocaleManager
}