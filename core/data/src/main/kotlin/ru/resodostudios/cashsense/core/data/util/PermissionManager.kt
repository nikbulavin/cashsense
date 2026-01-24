package ru.resodostudios.cashsense.core.data.util

import kotlinx.coroutines.flow.Flow

interface PermissionManager {

    val shouldRequestNotifications: Flow<Boolean>
}