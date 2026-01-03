package ru.resodostudios.cashsense.feature.transaction.detail.impl

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.time.format.FormatStyle
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransactionScreen(
    onBackClick: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel(),
) {
    val transactionUiState by viewModel.transactionUiState.collectAsStateWithLifecycle()

    TransactionScreen(
        transactionState = transactionUiState,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransactionScreen(
    transactionState: TransactionUiState,
    onBackClick: () -> Unit,
) {
    when (transactionState) {
        TransactionUiState.Loading -> LoadingState(Modifier.fillMaxSize())
        is TransactionUiState.Success -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = transactionState.transaction.timestamp.formatDate(
                                    DateFormatType.DATE_TIME,
                                    FormatStyle.MEDIUM,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        subtitle = {},
                        navigationIcon = {
                            CsIconButton(
                                onClick = onBackClick,
                                icon = CsIcons.Outlined.ArrowBack,
                                contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                                tooltipPosition = TooltipAnchorPosition.Right,
                            )
                        },
                        titleHorizontalAlignment = Alignment.CenterHorizontally,
                    )
                }
            ) { innerPadding ->

            }
        }
    }
}
