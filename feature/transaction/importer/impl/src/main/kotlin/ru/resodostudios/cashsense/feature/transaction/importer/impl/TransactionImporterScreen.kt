package ru.resodostudios.cashsense.feature.transaction.importer.impl

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonDefaults.smallContainerSize
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.result.ResultEffect
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.component.button.CsButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsFilledIconButton
import ru.resodostudios.cashsense.core.designsystem.component.button.CsIconButton
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.DocumentSearch
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.model.data.CsvConfig
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.model.data.Transaction
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
    onTransactionEdit: (Transaction) -> Unit,
    viewModel: TransactionImporterViewModel = hiltViewModel(),
) {
    val transactionImporterUiState by viewModel.transactionImporterUiState.collectAsStateWithLifecycle()

    ResultEffect<Transaction> {
        viewModel.updateParsedTransaction(it)
    }

    TransactionImporterScreen(
        transactionImporterUiState = transactionImporterUiState,
        onBackClick = onBackClick,
        onFileSelected = viewModel::handleFileSelected,
        onConfigUpdate = viewModel::updateConfig,
        onTransactionEdit = onTransactionEdit,
        onTransactionSelect = viewModel::toggleTransactionSelection,
        onImportClick = viewModel::importTransactions,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TransactionImporterScreen(
    transactionImporterUiState: TransactionImporterUiState,
    onBackClick: () -> Unit,
    onFileSelected: (String, List<String>) -> Unit,
    onConfigUpdate: (CsvConfig) -> Unit,
    onTransactionEdit: (Transaction) -> Unit,
    onTransactionSelect: (String) -> Unit,
    onImportClick: () -> Unit,
) {
    TrackScreenViewEvent(screenName = "TransactionImporter")

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
                    CsFilledIconButton(
                        tooltipPosition = TooltipAnchorPosition.Left,
                        onClick = {
                            onImportClick()
                            onBackClick()
                        },
                        enabled = transactionImporterUiState.selectedTransactions.isNotEmpty(),
                        contentDescription = stringResource(localesR.string.import_title),
                        icon = CsIcons.Outlined.Check,
                        modifier = Modifier
                            .size(smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                    )
                },
            )
        },
    ) { innerPadding ->
        if (transactionImporterUiState.isLoading) {
            LoadingState(Modifier.fillMaxSize())
        } else {
            LazyColumn(
                contentPadding = innerPadding + PaddingValues(16.dp),
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CsvPickerCard(
                            fileName = transactionImporterUiState.fileName,
                            onFileSelected = onFileSelected,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        if (transactionImporterUiState.lines.isNotEmpty()) {
                            Text(
                                text = stringResource(localesR.string.column_mapping),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            MappingField(
                                label = stringResource(localesR.string.date),
                                selectedIndex = transactionImporterUiState.config.dateColumnIndex,
                                columns = transactionImporterUiState.columns,
                                onColumnSelected = {
                                    onConfigUpdate(
                                        transactionImporterUiState.config.copy(
                                            dateColumnIndex = it,
                                        )
                                    )
                                },
                            )
                            MappingField(
                                label = stringResource(localesR.string.amount),
                                selectedIndex = transactionImporterUiState.config.amountColumnIndex,
                                columns = transactionImporterUiState.columns,
                                onColumnSelected = {
                                    onConfigUpdate(
                                        transactionImporterUiState.config.copy(
                                            amountColumnIndex = it,
                                        )
                                    )
                                },
                            )
                            MappingField(
                                label = stringResource(localesR.string.description),
                                selectedIndex = transactionImporterUiState.config.descriptionColumnIndex,
                                columns = transactionImporterUiState.columns,
                                onColumnSelected = {
                                    onConfigUpdate(
                                        transactionImporterUiState.config.copy(
                                            descriptionColumnIndex = it,
                                        )
                                    )
                                },
                            )
                            MappingField(
                                label = stringResource(localesR.string.category_title),
                                selectedIndex = transactionImporterUiState.config.categoryColumnIndex,
                                columns = transactionImporterUiState.columns,
                                onColumnSelected = {
                                    onConfigUpdate(
                                        transactionImporterUiState.config.copy(
                                            categoryColumnIndex = it,
                                        )
                                    )
                                },
                            )

                            DateFormatField(
                                value = transactionImporterUiState.config.dateFormat,
                                onValueChange = {
                                    onConfigUpdate(
                                        transactionImporterUiState.config.copy(
                                            dateFormat = it,
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            TextField(
                                value = transactionImporterUiState.config.columnSeparator,
                                onValueChange = {
                                    onConfigUpdate(
                                        transactionImporterUiState.config.copy(
                                            columnSeparator = it,
                                        )
                                    )
                                },
                                label = { Text(stringResource(localesR.string.column_separator)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.tonalColors(),
                                shape = TextFieldDefaults.roundedShape,
                            )
                        }
                    }
                }

                if (transactionImporterUiState.parsedTransactions.isNotEmpty() &&
                    transactionImporterUiState.currency != null
                ) {
                    item {
                        Text(
                            text = stringResource(localesR.string.preview),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 24.dp),
                        )
                    }
                    transactionImporterUiState.parsedTransactions
                        .groupByDate()
                        .forEach { transactionGroup ->
                            item {
                                CsTag(
                                    text = transactionGroup.key.formatDate(
                                        DateFormatType.DATE,
                                        FormatStyle.MEDIUM,
                                    ),
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(vertical = 16.dp),
                                )
                            }
                            itemsIndexed(
                                items = transactionGroup.value,
                                key = { _, transaction -> transaction.id },
                            ) { index, transaction ->
                                val selected =
                                    transaction.id in transactionImporterUiState.selectedTransactions
                                TransactionItem(
                                    transaction = transaction,
                                    currency = transactionImporterUiState.currency,
                                    selected = selected,
                                    onClick = { onTransactionEdit(transaction) },
                                    shapes = if (transactionGroup.value.size == 1) {
                                        ListItemDefaults.shapes(shape = RoundedCornerShape(16.dp))
                                    } else {
                                        ListItemDefaults.segmentedShapes(
                                            index = index,
                                            count = transactionGroup.value.size,
                                        )
                                    },
                                    trailingContent = {
                                        Checkbox(
                                            checked = selected,
                                            onCheckedChange = { onTransactionSelect(transaction.id) },
                                        )
                                    },
                                )
                                if (index != transactionGroup.value.lastIndex) {
                                    Spacer(Modifier.height(ListItemDefaults.SegmentedGap))
                                }
                            }
                        }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateFormatField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(stringResource(localesR.string.date_format)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            colors = TextFieldDefaults.tonalColors(),
            shape = TextFieldDefaults.roundedShape,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MenuDefaults.standaloneGroupShape,
            containerColor = MenuDefaults.groupVibrantContainerColor,
        ) {
            COMMON_DATE_TIME_PATTERNS.forEachIndexed { index, pattern ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = pattern,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = {
                        onValueChange(pattern)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    shapes = MenuDefaults.itemShape(index, COMMON_DATE_TIME_PATTERNS.size),
                    selected = pattern == value,
                    selectedLeadingIcon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Check,
                            contentDescription = null,
                        )
                    },
                    colors = MenuDefaults.selectableItemVibrantColors(),
                )
            }
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
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        TextField(
            value = columns.getOrNull(selectedIndex) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            colors = TextFieldDefaults.tonalColors(),
            shape = TextFieldDefaults.roundedShape,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MenuDefaults.standaloneGroupShape,
            containerColor = MenuDefaults.groupVibrantContainerColor,
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
                    shapes = MenuDefaults.itemShape(index, columns.size),
                    selected = index == selectedIndex,
                    selectedLeadingIcon = {
                        Icon(
                            imageVector = CsIcons.Outlined.Check,
                            contentDescription = null,
                        )
                    },
                    colors = MenuDefaults.selectableItemVibrantColors(),
                )
            }
        }
    }
}

@Composable
private fun CsvPickerCard(
    fileName: String,
    onFileSelected: (String, List<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let {
            var name = ""
            var lines = emptyList<String>()
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst()) {
                    name = cursor.getString(nameIndex)
                }
            }
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                lines = inputStream.bufferedReader().readLines()
            }
            onFileSelected(name, lines)
        }
    }
    OutlinedCard(
        modifier = modifier.animateContentSize(),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(localesR.string.select_csv_file_description),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
            )
            AnimatedVisibility(fileName.isNotEmpty()) {
                Text(
                    text = fileName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            CsButton(
                onClick = {
                    filePickerLauncher.launch(
                        arrayOf(
                            "text/comma-separated-values",
                            "text/csv",
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                icon = CsIcons.Filled.DocumentSearch,
                title = stringResource(localesR.string.select_file),
            )
        }
    }
}

private val COMMON_DATE_TIME_PATTERNS = listOf(
    "dd.MM.yyyy HH:mm:ss",
    "yyyy-MM-dd HH:mm:ss",
    "dd/MM/yyyy HH:mm:ss",
    "yyyy/MM/dd HH:mm:ss",
    "dd-MM-yyyy HH:mm:ss",
)
