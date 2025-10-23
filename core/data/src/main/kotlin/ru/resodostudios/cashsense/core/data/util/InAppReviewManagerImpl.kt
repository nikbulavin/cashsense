package ru.resodostudios.cashsense.core.data.util

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import javax.inject.Inject

internal class InAppReviewManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionsRepository: TransactionsRepository,
) : InAppReviewManager {

    private val reviewManager by lazy { ReviewManagerFactory.create(context) }

    override suspend fun openReviewDialog(activity: Activity) {
        val transactionsCount = transactionsRepository.getTransactionsCount().first()
        if (transactionsCount < MIN_TRANSACTIONS_FOR_REVIEW) return
        runCatching {
            reviewManager.launchReviewFlow(activity, reviewManager.requestReviewFlow().await())
        }
    }
}

private const val MIN_TRANSACTIONS_FOR_REVIEW = 15