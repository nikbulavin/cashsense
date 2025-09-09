package ru.resodostudios.cashsense.core.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.InboxStyle
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaZoneId
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.SUBSCRIPTIONS_PATH
import ru.resodostudios.cashsense.core.util.Constants.TARGET_ACTIVITY_NAME
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import ru.resodostudios.cashsense.core.locales.R as localesR

private const val SUBSCRIPTIONS_NOTIFICATION_REQUEST_CODE = 0
private const val SUBSCRIPTIONS_NOTIFICATION_SUMMARY_ID = 1
private const val SUBSCRIPTIONS_NOTIFICATION_CHANNEL_ID = ""
private const val SUBSCRIPTIONS_NOTIFICATION_GROUP = "SUBSCRIPTIONS_NOTIFICATIONS"

/**
 * Implementation of [Notifier] that displays notifications in the system tray.
 */
@Singleton
internal class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {

    override fun postSubscriptionNotification(
        subscription: Subscription,
    ) = with(context) {
        if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) return

        val subscriptionNotification = createSubscriptionNotification {
            val price = subscription.amount.formatAmount(subscription.currency)
            val date = subscription.paymentDate.formatDate()
            val contentText = getString(localesR.string.subscriptions_notification_content_text, price, date)
            setSmallIcon(R.drawable.ic_outlined_payments)
                .setContentTitle(subscription.title)
                .setContentText(contentText)
                .setContentIntent(subscriptionPendingIntent())
                .setGroup(SUBSCRIPTIONS_NOTIFICATION_GROUP)
                .setAutoCancel(true)
        }
        val summaryNotification = createSubscriptionNotification {
            val title = getString(localesR.string.subscriptions_title)
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_outlined_payments)
                .setStyle(subscriptionsNotificationStyle(subscription, title))
                .setGroup(SUBSCRIPTIONS_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(
            subscription.id.hashCode(),
            subscriptionNotification,
        )
        notificationManager.notify(SUBSCRIPTIONS_NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    /**
     * Creates an inbox style summary notification for subscriptions
     */
    private fun subscriptionsNotificationStyle(
        subscription: Subscription,
        title: String,
    ): InboxStyle = InboxStyle()
        .addLine(subscription.title)
        .setBigContentTitle(title)
        .setSummaryText(title)
}

/**
 * Creates a notification for configured for subscription
 */
private fun Context.createSubscriptionNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        SUBSCRIPTIONS_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures that a notification channel is present if applicable
 */
private fun Context.ensureNotificationChannelExists() {
    val channel = NotificationChannel(
        SUBSCRIPTIONS_NOTIFICATION_CHANNEL_ID,
        getString(localesR.string.subscriptions_title),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(localesR.string.subscriptions_notification_channel_description)
    }
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.subscriptionPendingIntent(): PendingIntent? = PendingIntent.getActivity(
    this,
    SUBSCRIPTIONS_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = "$DEEP_LINK_SCHEME_AND_HOST/$SUBSCRIPTIONS_PATH".toUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

private fun Instant.formatDate(): String =
    DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .withZone(TimeZone.currentSystemDefault().toJavaZoneId())
        .format(toJavaInstant())