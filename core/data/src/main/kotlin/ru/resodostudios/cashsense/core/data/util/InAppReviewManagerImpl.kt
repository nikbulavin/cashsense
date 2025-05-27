package ru.resodostudios.cashsense.core.data.util

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class InAppReviewManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionsRepository: TransactionsRepository,
) : InAppReviewManager {

    private val reviewManager = ReviewManagerFactory.create(context)

    override suspend fun openReviewDialog(activity: Activity) {
        val isTransactionsEnough = transactionsRepository.getTransactionsCount().first() >= 15
        reviewManager.requestReviewFlow().addOnCompleteListener { request ->
            if (request.isSuccessful && isTransactionsEnough) {
                val reviewInfo = request.result
                reviewManager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }
}