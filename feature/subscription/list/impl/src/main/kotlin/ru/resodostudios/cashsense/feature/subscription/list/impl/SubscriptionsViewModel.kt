package ru.resodostudios.cashsense.feature.subscription.list.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.model.data.Subscription
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
internal class SubscriptionsViewModel @Inject constructor(
    private val subscriptionsRepository: SubscriptionsRepository,
) : ViewModel() {

    val subscriptionsUiState: StateFlow<SubscriptionsUiState> = subscriptionsRepository.getSubscriptions()
        .map { subscriptions ->
            if (subscriptions.isEmpty()) {
                SubscriptionsUiState.Empty
            } else {
                SubscriptionsUiState.Success(subscriptions = subscriptions)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = SubscriptionsUiState.Loading,
        )

    fun deleteSubscription(subscriptionId: String) {
        viewModelScope.launch {
            subscriptionsRepository.deleteSubscription(subscriptionId)
        }
    }
}

sealed interface SubscriptionsUiState {

    data object Empty : SubscriptionsUiState

    data object Loading : SubscriptionsUiState

    data class Success(
        val subscriptions: List<Subscription>,
    ) : SubscriptionsUiState
}