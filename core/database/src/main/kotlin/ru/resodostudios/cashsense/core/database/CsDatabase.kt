package ru.resodostudios.cashsense.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.resodostudios.cashsense.core.database.dao.CategoryDao
import ru.resodostudios.cashsense.core.database.dao.CurrencyConversionDao
import ru.resodostudios.cashsense.core.database.dao.SubscriptionDao
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.dao.WalletDao
import ru.resodostudios.cashsense.core.database.model.CategoryEntity
import ru.resodostudios.cashsense.core.database.model.CurrencyExchangeRateEntity
import ru.resodostudios.cashsense.core.database.model.SubscriptionEntity
import ru.resodostudios.cashsense.core.database.model.TransactionCategoryCrossRefEntity
import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import ru.resodostudios.cashsense.core.database.model.WalletEntity
import ru.resodostudios.cashsense.core.database.util.BigDecimalConverter
import ru.resodostudios.cashsense.core.database.util.CurrencyConverter
import ru.resodostudios.cashsense.core.database.util.InstantConverter
import ru.resodostudios.cashsense.core.database.util.StatusTypeConverter
import ru.resodostudios.cashsense.core.database.util.UuidConverter

@Database(
    entities = [
        CategoryEntity::class,
        CurrencyExchangeRateEntity::class,
        SubscriptionEntity::class,
        TransactionEntity::class,
        TransactionCategoryCrossRefEntity::class,
        WalletEntity::class,
    ],
    version = 10,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = DatabaseMigrations.Schema1to2::class),
        AutoMigration(from = 2, to = 3, spec = DatabaseMigrations.Schema2to3::class),
        AutoMigration(from = 3, to = 4, spec = DatabaseMigrations.Schema3to4::class),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10, spec = DatabaseMigrations.Schema9to10::class),
    ],
    exportSchema = true,
)
@TypeConverters(
    BigDecimalConverter::class,
    CurrencyConverter::class,
    InstantConverter::class,
    StatusTypeConverter::class,
    UuidConverter::class,
)
internal abstract class CsDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun currencyConversionDao(): CurrencyConversionDao

    abstract fun subscriptionDao(): SubscriptionDao

    abstract fun transactionDao(): TransactionDao

    abstract fun walletDao(): WalletDao
}