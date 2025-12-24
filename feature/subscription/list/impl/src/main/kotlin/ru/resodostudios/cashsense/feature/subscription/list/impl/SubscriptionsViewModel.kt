package ru.resodostudios.cashsense.feature.subscription.list.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.model.data.Subscription
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val subscriptionsRepository: SubscriptionsRepository,
) : ViewModel() {

    private val shouldDisplayUndoSubscriptionState = MutableStateFlow(false)
    private val lastRemovedSubscriptionState = MutableStateFlow<Subscription?>(null)
    private val selectedSubscriptionState = MutableStateFlow<Subscription?>(null)

    val subscriptionsUiState: StateFlow<SubscriptionsUiState> = combine(
        subscriptionsRepository.getSubscriptions(),
        shouldDisplayUndoSubscriptionState,
        selectedSubscriptionState,
        SubscriptionsUiState::Success,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = SubscriptionsUiState.Loading,
        )

    fun deleteSubscription() {
        viewModelScope.launch {
            lastRemovedSubscriptionState.value = selectedSubscriptionState.value
            selectedSubscriptionState.value?.id?.let {
                subscriptionsRepository.deleteSubscription(it)
            }
            shouldDisplayUndoSubscriptionState.value = true
        }
    }

    fun updateSelectedSubscription(subscription: Subscription) {
        selectedSubscriptionState.value = subscription
    }

    fun undoSubscriptionRemoval() {
        viewModelScope.launch {
            lastRemovedSubscriptionState.value?.let {
                subscriptionsRepository.upsertSubscription(it)
            }
            clearUndoState()
        }
    }

    fun clearUndoState() {
        lastRemovedSubscriptionState.value = null
        shouldDisplayUndoSubscriptionState.value = false
    }
}

sealed interface SubscriptionsUiState {

    data object Loading : SubscriptionsUiState

    data class Success(
        val subscriptions: List<Subscription>,
        val shouldDisplayUndoSubscription: Boolean,
        val selectedSubscription: Subscription?,
    ) : SubscriptionsUiState
}