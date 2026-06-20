package ru.resodostudios.cashsense.core.database

import androidx.room3.DeleteColumn
import androidx.room3.RenameColumn
import androidx.room3.migration.AutoMigrationSpec
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

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

    @RenameColumn(
        tableName = "subscriptions",
        fromColumnName = "alarm_notificationDate",
        toColumnName = "notification_date",
    )
    @DeleteColumn.Entries(
        DeleteColumn(
            tableName = "subscriptions",
            columnName = "alarm_repeatingInterval",
        ),
        DeleteColumn(
            tableName = "subscriptions",
            columnName = "alarm_id",
        ),
    )
    class Schema12to13 : AutoMigrationSpec
}

internal val Schema11to12 = object : Migration(11, 12) {
    override suspend fun migrate(connection: SQLiteConnection) {
        // Create a new transactions table with the new schema
        connection.execSQL(
            """
            CREATE TABLE `transactions_new` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `wallet_owner_id` TEXT NOT NULL,
                `description` TEXT,
                `amount` TEXT NOT NULL,
                `timestamp` INTEGER NOT NULL,
                `completed` INTEGER NOT NULL DEFAULT 1,
                `ignored` INTEGER NOT NULL DEFAULT 0,
                `transfer_id` TEXT,
                FOREIGN KEY(`wallet_owner_id`) REFERENCES `wallets`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """
        )

        // Copy data from the old table to the new one, converting `status` to `completed`
        connection.execSQL(
            """
            INSERT INTO `transactions_new` (id, wallet_owner_id, description, amount, timestamp, completed)
            SELECT id, wallet_owner_id, description, amount, timestamp,
                   CASE WHEN status = 'PENDING' THEN 0 ELSE 1 END
            FROM `transactions`
        """
        )

        // Drop the old transactions table
        connection.execSQL("DROP TABLE `transactions`")

        // Rename the new table to the original name
        connection.execSQL("ALTER TABLE `transactions_new` RENAME TO `transactions`")

        // Recreate indices
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_wallet_owner_id` ON `transactions` (`wallet_owner_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_transfer_id` ON `transactions` (`transfer_id`)")
    }
}

internal val Schema13to14 = object : Migration(13, 14) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `transactions_new` (
                `id` TEXT NOT NULL, 
                `wallet_owner_id` TEXT NOT NULL, 
                `description` TEXT, 
                `amount` TEXT NOT NULL, 
                `timestamp` INTEGER NOT NULL, 
                `completed` INTEGER NOT NULL DEFAULT 1, 
                `ignored` INTEGER NOT NULL DEFAULT 0, 
                `transfer_id` TEXT, 
                `category_id` TEXT, 
                PRIMARY KEY(`id`), 
                FOREIGN KEY(`wallet_owner_id`) REFERENCES `wallets`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, 
                FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
            )
        """.trimIndent()
        )

        connection.execSQL(
            """
            INSERT INTO `transactions_new` (
                `id`, `wallet_owner_id`, `description`, `amount`, `timestamp`, `completed`, `ignored`, `transfer_id`, `category_id`
            )
            SELECT 
                t.`id`, 
                t.`wallet_owner_id`, 
                t.`description`, 
                t.`amount`, 
                t.`timestamp`, 
                t.`completed`, 
                t.`ignored`, 
                t.`transfer_id`,
                (SELECT `category_id` FROM `transactions_categories` WHERE `transaction_id` = t.`id` LIMIT 1)
            FROM `transactions` t
        """.trimIndent()
        )

        connection.execSQL("DROP TABLE IF EXISTS `transactions_categories`")
        connection.execSQL("DROP TABLE IF EXISTS `transactions`")

        connection.execSQL("ALTER TABLE `transactions_new` RENAME TO `transactions`")

        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_wallet_owner_id` ON `transactions` (`wallet_owner_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_transfer_id` ON `transactions` (`transfer_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_category_id` ON `transactions` (`category_id`)")
    }
}
