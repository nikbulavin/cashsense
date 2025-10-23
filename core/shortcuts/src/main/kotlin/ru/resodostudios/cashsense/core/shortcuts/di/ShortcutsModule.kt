package ru.resodostudios.cashsense.core.shortcuts.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.shortcuts.DynamicShortcutManager
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager

@Module
@InstallIn(SingletonComponent::class)
internal interface ShortcutsModule {

    @Binds
    fun bindsShortcutManager(impl: DynamicShortcutManager): ShortcutManager
}
