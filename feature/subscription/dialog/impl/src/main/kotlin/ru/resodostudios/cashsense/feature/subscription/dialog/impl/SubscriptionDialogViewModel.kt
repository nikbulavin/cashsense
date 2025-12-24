package ru.resodostudios.cashsense.feature.subscription.dialog.impl

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.model.data.Reminder
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.NONE
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.model.data.getRepeatingIntervalType
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.subscription.dialog.api.SubscriptionDialogNavKey
import java.util.Currency
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

@HiltViewModel(assistedFactory = SubscriptionDialogViewModel.Factory::class)
class SubscriptionDialogViewModel @AssistedInject constructor(
    private val userDataRepository: UserDataRepository,
    private val subscriptionsRepository: SubscriptionsRepository,
    @ApplicationScope private val appScope: CoroutineScope,
    @Assisted val key: SubscriptionDialogNavKey,
) : ViewModel() {

    private val _subscriptionDialogUiState = MutableStateFlow(SubscriptionDialogUiState())
    val subscriptionDialogUiState = _subscriptionDialogUiState.asStateFlow()

    init {
        val subscriptionId = key.subscriptionId
        if (subscriptionId != null) {
            loadSubscription(subscriptionId)
        } else {
            loadUserData()
        }
    }

    fun onSubscriptionEvent(event: SubscriptionDialogEvent) {
        when (event) {
            is SubscriptionDialogEvent.Save -> {
                appScope.launch {
                    subscriptionsRepository.upsertSubscription(event.subscription)
                }
            }

            is SubscriptionDialogEvent.UpdateTitle -> {
                _subscriptionDialogUiState.update {
                    it.copy(title = event.title)
                }
            }

            is SubscriptionDialogEvent.UpdateAmount -> {
                _subscriptionDialogUiState.update {
                    it.copy(amount = event.amount.cleanAmount())
                }
            }

            is SubscriptionDialogEvent.UpdateCurrency -> {
                _subscriptionDialogUiState.update {
                    it.copy(currency = event.currency)
                }
            }

            is SubscriptionDialogEvent.UpdatePaymentDate -> {
                _subscriptionDialogUiState.update {
                    it.copy(paymentDate = event.paymentDate)
                }
            }

            is SubscriptionDialogEvent.UpdateReminderSwitch -> {
                _subscriptionDialogUiState.update {
                    it.copy(isReminderEnabled = event.isReminderActive)
                }
            }

            is SubscriptionDialogEvent.UpdateRepeatingInterval -> {
                _subscriptionDialogUiState.update {
                    it.copy(repeatingInterval = event.repeatingInterval)
                }
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _subscriptionDialogUiState.update { SubscriptionDialogUiState(isLoading = true) }
            val userData = userDataRepository.userData.first()
            _subscriptionDialogUiState.update {
                SubscriptionDialogUiState(
                    currency = Currency.getInstance(userData.currency),
                )
            }
        }
    }

    private fun loadSubscription(id: String) {
        viewModelScope.launch {
            _subscriptionDialogUiState.update { SubscriptionDialogUiState(isLoading = true) }
            val subscription = subscriptionsRepository.getSubscription(id).first()
            _subscriptionDialogUiState.update {
                it.copy(
                    id = subscription.id,
                    title = subscription.title,
                    amount = subscription.amount.toString(),
                    paymentDate = subscription.paymentDate,
                    currency = subscription.currency,
                    isReminderEnabled = subscription.reminder != null,
                    repeatingInterval = getRepeatingIntervalType(subscription.reminder?.repeatingInterval),
                    isLoading = false,
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: SubscriptionDialogNavKey): SubscriptionDialogViewModel
    }
}

@Immutable
data class SubscriptionDialogUiState(
    val id: String = "",
    val title: String = "",
    val amount: String = "",
    val paymentDate: Instant = Clock.System.now(),
    val currency: Currency = getUsdCurrency(),
    val isReminderEnabled: Boolean = false,
    val repeatingInterval: RepeatingIntervalType = NONE,
    val isLoading: Boolean = false,
)

fun SubscriptionDialogUiState.asSubscription(): Subscription {
    val subscriptionId = id.ifBlank { Uuid.random().toHexString() }
    var reminder: Reminder? = null

    if (isReminderEnabled) {
        val timeZone = TimeZone.currentSystemDefault()
        val currentDateTime = paymentDate.toLocalDateTime(timeZone)
        val previousDate = currentDateTime.date.minus(1, DateTimeUnit.DAY)
        val notificationDate = LocalDateTime(previousDate, LocalTime(9, 0)).toInstant(timeZone)
        reminder = Reminder(
            id = subscriptionId.hashCode(),
            notificationDate = notificationDate,
            repeatingInterval = repeatingInterval.period,
        )
    }

    return Subscription(
        id = subscriptionId,
        title = title.trim(),
        amount = amount.toBigDecimal(),
        paymentDate = paymentDate,
        currency = currency,
        reminder = reminder,
    )
}