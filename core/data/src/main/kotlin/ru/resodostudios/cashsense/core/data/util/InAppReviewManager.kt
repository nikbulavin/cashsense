package ru.resodostudios.cashsense.core.data.util

import android.app.Activity

interface InAppReviewManager {

    suspend fun openReviewDialog(activity: Activity)
}