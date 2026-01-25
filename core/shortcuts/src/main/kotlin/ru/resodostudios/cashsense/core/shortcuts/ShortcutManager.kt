package ru.resodostudios.cashsense.core.shortcuts

interface ShortcutManager {

    fun addTransactionShortcut(walletId: String)

    fun removeShortcuts()

    fun syncTransactionShortcut()
}