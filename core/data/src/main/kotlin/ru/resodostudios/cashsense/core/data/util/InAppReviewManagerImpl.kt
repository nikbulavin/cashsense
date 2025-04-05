package ru.resodostudios.cashsense.core.data.util

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class InAppReviewManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : InAppReviewManager {

    private val reviewManager = ReviewManagerFactory.create(context)

    override suspend fun openReviewDialog(activity: Activity) {
        reviewManager.requestReviewFlow().addOnCompleteListener { request ->
            if (request.isSuccessful) {
                val reviewInfo = request.result
                reviewManager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }
}