package ru.resodostudios.cashsense.core.database

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec

/**
 * Automatic schema migrations sometimes require extra instructions to perform the migration, for
 * example, when a column is renamed. These extra instructions are placed here by creating a class
 * using the following naming convention `SchemaXtoY` where X is the schema version you're migrating
 * from and Y is the schema version you're migrating to. The class should implement
 * `AutoMigrationSpec`.
 */
internal object DatabaseMigrations {

    @RenameColumn(
        tableName = "categories",
        fromColumnName = "icon",
        toColumnName = "icon_id",
    )
    class Schema1to2 : AutoMigrationSpec

    @RenameColumn(
        tableName = "transactions",
        fromColumnName = "date",
        toColumnName = "timestamp",
    )
    class Schema2to3 : AutoMigrationSpec

    @RenameColumn.Entries(
        RenameColumn(
            tableName = "subscriptions",
            fromColumnName = "notification_date",
            toColumnName = "alarm_notificationDate",
        ),
        RenameColumn(
            tableName = "subscriptions",
            fromColumnName = "repeating_interval",
            toColumnName = "alarm_repeatingInterval",
        ),
    )
    class Schema3to4 : AutoMigrationSpec

    @RenameColumn(
        tableName = "currency_exchange_rates",
        fromColumnName = "uuid",
        toColumnName = "id",
    )
    class Schema9to10 : AutoMigrationSpec
}