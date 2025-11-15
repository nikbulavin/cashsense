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
internal interface DataModule {

    @Binds
    fun bindsCategoriesRepository(impl: OfflineCategoriesRepository): CategoriesRepository

    @Binds
    fun bindsCurrencyConversionRepository(impl: OfflineFirstCurrencyConversionRepository): CurrencyConversionRepository

    @Binds
    fun bindsTransactionsRepository(impl: OfflineTransactionRepository): TransactionsRepository

    @Binds
    fun bindsWalletsRepository(impl: OfflineWalletsRepository): WalletsRepository

    @Binds
    fun bindsSubscriptionsRepository(impl: OfflineSubscriptionsRepository): SubscriptionsRepository

    @Binds
    fun bindsUserDataRepository(impl: OfflineUserDataRepository): UserDataRepository

    @Binds
    fun bindsTimeZoneMonitor(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor

    @Binds
    fun bindsReminderScheduler(impl: ReminderSchedulerImpl): ReminderScheduler

    @Binds
    fun bindsInAppUpdateManager(impl: InAppUpdateManagerImpl): InAppUpdateManager

    @Binds
    fun bindsInAppReviewManager(impl: InAppReviewManagerImpl): InAppReviewManager

    @Binds
    fun bindsAppLocaleManager(impl: AppLocaleManagerImpl): AppLocaleManager
}