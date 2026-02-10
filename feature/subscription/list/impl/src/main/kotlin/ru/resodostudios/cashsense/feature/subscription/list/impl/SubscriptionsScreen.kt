package ru.resodostudios.cashsense.feature.subscription.list.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells.Adaptive
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsTopAppBar
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.MessageWithAnimation
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun SubscriptionsScreen(
    onEditSubscription: (String) -> Unit,
    viewModel: SubscriptionsViewModel = hiltViewModel(),
) {
    val subscriptionsState by viewModel.subscriptionsUiState.collectAsStateWithLifecycle()

    SubscriptionsScreen(
        subscriptionsState = subscriptionsState,
        onSubscriptionEdit = onEditSubscription,
        onSubscriptionDelete = viewModel::deleteSubscription,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubscriptionsScreen(
    subscriptionsState: SubscriptionsUiState,
    onSubscriptionEdit: (String) -> Unit = {},
    onSubscriptionDelete: (String) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            CsTopAppBar(
                titleRes = localesR.string.subscriptions_title,
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    scrolledContainerColor = Color.Transparent,
                    containerColor = Color.Transparent,
                ),
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        when (subscriptionsState) {
            SubscriptionsUiState.Empty -> {
                MessageWithAnimation(
                    messageRes = localesR.string.subscriptions_empty,
                    animationRes = R.raw.anim_subscriptions_empty,
                    modifier = Modifier.padding(innerPadding),
                )
            }

            SubscriptionsUiState.Loading -> {
                LoadingState(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                )
            }

            is SubscriptionsUiState.Success -> {
                LazyVerticalGrid(
                    columns = Adaptive(300.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding() + 110.dp,
                    ),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(subscriptionsState.subscriptions) { subscription ->
                        SubscriptionCard(
                            subscription = subscription,
                            onEditClick = onSubscriptionEdit,
                            onDeleteClick = onSubscriptionDelete,
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "Subscriptions")
}

@Preview
@Composable
private fun SubscriptionsGridPreview(
    @PreviewParameter(SubscriptionPreviewParameterProvider::class)
    subscriptions: List<Subscription>,
) {
    CsTheme {
        Surface {
            SubscriptionsScreen(
                subscriptionsState = SubscriptionsUiState.Success(
                    subscriptions = subscriptions,
                ),
                onSubscriptionEdit = {},
            )
        }
    }
}