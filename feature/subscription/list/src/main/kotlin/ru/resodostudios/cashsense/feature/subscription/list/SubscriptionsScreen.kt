package ru.resodostudios.cashsense.feature.subscription.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.ui.component.EmptyState
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.feature.subscription.list.SubscriptionsUiState.Loading
import ru.resodostudios.cashsense.feature.subscription.list.SubscriptionsUiState.Success
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun SubscriptionsScreen(
    onEditSubscription: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: SubscriptionsViewModel = hiltViewModel(),
) {
    val subscriptionsState by viewModel.subscriptionsUiState.collectAsStateWithLifecycle()

    SubscriptionsScreen(
        subscriptionsState = subscriptionsState,
        onEditSubscription = onEditSubscription,
        onShowSnackbar = onShowSnackbar,
        onSelectSubscription = viewModel::updateSelectedSubscription,
        onDeleteSubscription = viewModel::deleteSubscription,
        undoSubscriptionRemoval = viewModel::undoSubscriptionRemoval,
        clearUndoState = viewModel::clearUndoState,
    )
}

@Composable
internal fun SubscriptionsScreen(
    subscriptionsState: SubscriptionsUiState,
    onEditSubscription: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onSelectSubscription: (Subscription) -> Unit,
    onDeleteSubscription: () -> Unit = {},
    undoSubscriptionRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    when (subscriptionsState) {
        Loading -> LoadingState(Modifier.fillMaxSize())
        is Success -> {
            val subscriptionDeletedMessage = stringResource(localesR.string.subscription_deleted)
            val undoText = stringResource(localesR.string.undo)

            LaunchedEffect(subscriptionsState.shouldDisplayUndoSubscription) {
                if (subscriptionsState.shouldDisplayUndoSubscription) {
                    val snackBarResult = onShowSnackbar(subscriptionDeletedMessage, undoText)
                    if (snackBarResult) {
                        undoSubscriptionRemoval()
                    } else {
                        clearUndoState()
                    }
                }
            }
            LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
                clearUndoState()
            }

            var showSubscriptionBottomSheet by rememberSaveable { mutableStateOf(false) }

            if (subscriptionsState.subscriptions.isNotEmpty()) {
                SubscriptionsGrid(
                    subscriptions = subscriptionsState.subscriptions,
                    onSubscriptionClick = {
                        onSelectSubscription(it)
                        showSubscriptionBottomSheet = true
                    },
                    modifier = Modifier.fillMaxSize(),
                )
                if (showSubscriptionBottomSheet && subscriptionsState.selectedSubscription != null) {
                    SubscriptionBottomSheet(
                        subscription = subscriptionsState.selectedSubscription,
                        onDismiss = { showSubscriptionBottomSheet = false },
                        onEdit = onEditSubscription,
                        onDelete = onDeleteSubscription,
                    )
                }
            } else {
                EmptyState(
                    messageRes = localesR.string.subscriptions_empty,
                    animationRes = R.raw.anim_subscriptions_empty,
                )
            }
        }
    }
}

@Composable
private fun SubscriptionsGrid(
    subscriptions: List<Subscription>,
    onSubscriptionClick: (Subscription) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 110.dp,
        ),
        modifier = modifier,
    ) {
        items(subscriptions) { subscription ->
            SubscriptionCard(
                subscription = subscription,
                onClick = onSubscriptionClick,
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Preview
@Composable
private fun SubscriptionsGridPreview(
    @PreviewParameter(SubscriptionPreviewParameterProvider::class)
    subscriptions: List<Subscription>,
) {
    CsTheme {
        Surface {
            SubscriptionsGrid(
                subscriptions = subscriptions,
                onSubscriptionClick = {},
            )
        }
    }
}