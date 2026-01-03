package ru.resodostudios.cashsense.feature.transaction.detail.impl

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.SendMoney
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.StoredIcon
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
            val transaction = transactionState.transaction
            val category = transactionState.transaction.category
            val (categoryIcon, categoryTitle) = if (transaction.transferId != null) {
                CsIcons.Outlined.SendMoney to stringResource(localesR.string.transfers)
            } else {
                val iconId = category?.iconId ?: StoredIcon.TRANSACTION.storedId
                val title = category?.title ?: stringResource(localesR.string.uncategorized)
                StoredIcon.asImageVector(iconId) to title
            }
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = transaction.timestamp.formatDate(
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialShapes.Cookie7Sided.toShape(),
                        modifier = Modifier.size(128.dp),
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = stringResource(localesR.string.pending),
                            modifier = Modifier.padding(32.dp),
                        )
                    }
                    transaction.description?.let {
                        TransactionDescription(
                            description = it,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransactionDescription(
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(localesR.string.description),
            style = MaterialTheme.typography.labelLarge,
            modifier = modifier.padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        ) {
            Text(
                text = description,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
