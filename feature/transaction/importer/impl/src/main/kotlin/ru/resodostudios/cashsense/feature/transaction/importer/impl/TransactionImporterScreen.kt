package ru.resodostudios.cashsense.feature.transaction.importer.impl

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.TransactionItem
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.core.ui.util.TrackScreenViewEvent
import ru.resodostudios.cashsense.core.ui.util.formatDate
import java.time.format.FormatStyle
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransactionImporterScreen(
    onBackClick: () -> Unit,
    viewModel: TransactionImporterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TransactionImporterScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onFileSelected = viewModel::handleFileSelected,
        onConfigUpdate = viewModel::updateConfig,
        onTransactionClick = viewModel::toggleTransactionSelection,
        onImportClick = viewModel::importTransactions,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransactionImporterScreen(
    uiState: TransactionImporterUiState,
    onBackClick: () -> Unit,
    onFileSelected: (List<String>) -> Unit,
    onConfigUpdate: (ru.resodostudios.cashsense.core.model.data.CsvConfig) -> Unit,
    onTransactionClick: (String) -> Unit,
    onImportClick: () -> Unit,
) {
    TrackScreenViewEvent(screenName = "TransactionImporter")

    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let {
            val lines = context.contentResolver.openInputStream(it)?.bufferedReader()?.readLines() ?: emptyList()
            onFileSelected(lines)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(localesR.string.import_csv),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    CsIconButton(
                        onClick = onBackClick,
                        icon = CsIcons.Outlined.ArrowBack,
                        contentDescription = stringResource(localesR.string.navigation_back_icon_description),
                        tooltipPosition = TooltipAnchorPosition.Right,
                    )
                },
                actions = {
                    FilledIconButton(
                        onClick = onImportClick,
                        enabled = uiState.selectedTransactions.isNotEmpty(),
                    ) {
                        Icon(
                            imageVector = CsIcons.Outlined.Check,
                            contentDescription = stringResource(localesR.string.import_title),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingState(Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Button(
                            onClick = {
                                filePickerLauncher.launch(arrayOf("text/comma-separated-values", "text/csv"))
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = if (uiState.lines.isEmpty()) {
                                    stringResource(localesR.string.select_file)
                                } else {
                                    stringResource(localesR.string.change_file)
                                },
                            )
                        }

                        if (uiState.lines.isNotEmpty()) {
                            Text(
                                text = stringResource(localesR.string.column_mapping),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            MappingField(
                                label = stringResource(localesR.string.date),
                                selectedIndex = uiState.config.dateColumnIndex,
                                columns = uiState.columns,
                                onColumnSelected = {
                                    onConfigUpdate(uiState.config.copy(dateColumnIndex = it))
                                },
                            )
                            MappingField(
                                label = stringResource(localesR.string.amount),
                                selectedIndex = uiState.config.amountColumnIndex,
                                columns = uiState.columns,
                                onColumnSelected = {
                                    onConfigUpdate(uiState.config.copy(amountColumnIndex = it))
                                },
                            )
                            MappingField(
                                label = stringResource(localesR.string.description),
                                selectedIndex = uiState.config.descriptionColumnIndex,
                                columns = uiState.columns,
                                onColumnSelected = {
                                    onConfigUpdate(uiState.config.copy(descriptionColumnIndex = it))
                                },
                            )
                            MappingField(
                                label = stringResource(localesR.string.category_title),
                                selectedIndex = uiState.config.categoryColumnIndex,
                                columns = uiState.columns,
                                onColumnSelected = {
                                    onConfigUpdate(uiState.config.copy(categoryColumnIndex = it))
                                },
                            )

                            OutlinedTextField(
                                value = uiState.config.dateFormat,
                                onValueChange = { onConfigUpdate(uiState.config.copy(dateFormat = it)) },
                                label = { Text(stringResource(localesR.string.date_format)) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            OutlinedTextField(
                                value = uiState.config.columnSeparator,
                                onValueChange = { onConfigUpdate(uiState.config.copy(columnSeparator = it)) },
                                label = { Text(stringResource(localesR.string.column_separator)) },
                                modifier = Modifier.fillMaxWidth(),
                            )

                            HorizontalDivider()

                            if (uiState.parsedTransactions.isNotEmpty()) {
                                Text(
                                    text = stringResource(localesR.string.preview),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        } else {
                            Text(stringResource(localesR.string.select_csv_file_description))
                        }
                    }
                }

                if ((uiState.parsedTransactions.isNotEmpty()) && (uiState.currency != null)) {
                    val groupedTransactions = uiState.parsedTransactions.groupByDate()
                    groupedTransactions.forEach { transactionGroup ->
                        item {
                            CsTag(
                                text = transactionGroup.key.formatDate(DateFormatType.DATE, FormatStyle.MEDIUM),
                                color = Color.Transparent,
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                    .clip(CircleShape),
                            )
                        }
                        itemsIndexed(
                            items = transactionGroup.value,
                            key = { _, transaction -> transaction.id },
                        ) { index, transaction ->
                            TransactionItem(
                                transaction = transaction,
                                currency = uiState.currency,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                selected = uiState.selectedTransactions.contains(transaction.id),
                                onClick = { onTransactionClick(transaction.id) },
                                shapes = if (transactionGroup.value.size == 1) {
                                    ListItemDefaults.shapes(shape = RoundedCornerShape(16.dp))
                                } else {
                                    ListItemDefaults.segmentedShapes(index, transactionGroup.value.size)
                                },
                            )
                            if (index != transactionGroup.value.lastIndex) {
                                Spacer(Modifier.height(ListItemDefaults.SegmentedGap))
                            }
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }

    if (uiState.importFinished) {
        CsAlertDialog(
            titleRes = localesR.string.import_finished,
            confirmButtonTextRes = localesR.string.ok,
            dismissButtonTextRes = localesR.string.cancel,
            icon = CsIcons.Outlined.Check,
            onConfirm = onBackClick,
            onDismiss = onBackClick,
        ) {
            Text(
                stringResource(
                    localesR.string.imported_transactions_count,
                    uiState.importedCount,
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MappingField(
    label: String,
    selectedIndex: Int,
    columns: List<String>,
    onColumnSelected: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(value = false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = columns.getOrNull(selectedIndex) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MenuDefaults.standaloneGroupShape,
            containerColor = MenuDefaults.groupStandardContainerColor,
        ) {
            columns.forEachIndexed { index, column ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = column,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = {
                        onColumnSelected(index)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
