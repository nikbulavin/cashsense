package ru.resodostudios.cashsense.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.database.CsDatabase
import ru.resodostudios.cashsense.core.database.util.DatabaseTransferManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesCsDatabase(
        @ApplicationContext context: Context,
    ): CsDatabase = Room.databaseBuilder(
        context,
        CsDatabase::class.java,
        "cs-database",
    ).build()

    @Provides
    fun providesDatabaseTransferManager(
        @ApplicationContext context: Context,
        database: CsDatabase,
    ) = DatabaseTransferManager(
        context = context,
        databaseOpenHelper = database.openHelper,
    )
}