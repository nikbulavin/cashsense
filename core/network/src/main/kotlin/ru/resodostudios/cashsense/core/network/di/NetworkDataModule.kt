package ru.resodostudios.cashsense.core.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.network.CsNetworkDataSource
import ru.resodostudios.cashsense.core.network.ktor.KtorCsNetwork

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkDataModule {

    @Binds
    internal abstract fun bindsCsNetworkDataSource(
        csNetworkDataSource: KtorCsNetwork
    ): CsNetworkDataSource
}