package ru.resodostudios.core.navigation

import androidx.navigation3.runtime.NavKey

interface NavDeepLinkKey: NavKey {
    val parent: NavKey
}