package ru.resodostudios.cashsense.wallet.widget.impl

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface WalletWidgetEntryPoint {

    fun getExtendedUserWalletsUseCase(): GetExtendedUserWalletsUseCase
}