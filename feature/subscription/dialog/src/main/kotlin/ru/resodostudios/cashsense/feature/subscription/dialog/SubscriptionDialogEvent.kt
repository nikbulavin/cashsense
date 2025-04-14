package ru.resodostudios.cashsense.feature.subscription.dialog

import androidx.compose.runtime.Immutable
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType
import ru.resodostudios.cashsense.core.model.data.Subscription
import java.util.Currency

@Immutable
sealed interface SubscriptionDialogEvent {

    data class UpdateTitle(val title: String) : SubscriptionDialogEvent

    data class UpdateAmount(val amount: String) : SubscriptionDialogEvent

    data class UpdatePaymentDate(val paymentDate: Instant) : SubscriptionDialogEvent

    data class UpdateCurrency(val currency: Currency) : SubscriptionDialogEvent

    data class UpdateReminderSwitch(val isReminderActive: Boolean) : SubscriptionDialogEvent

    data class UpdateRepeatingInterval(val repeatingInterval: RepeatingIntervalType) : SubscriptionDialogEvent

    data class Save(val subscription: Subscription) : SubscriptionDialogEvent
}