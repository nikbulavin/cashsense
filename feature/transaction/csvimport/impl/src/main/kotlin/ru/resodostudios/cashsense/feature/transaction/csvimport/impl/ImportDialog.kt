package ru.resodostudios.cashsense.feature.transaction.csvimport.impl

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ReceiptLong
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun ImportDialog(
    onDismiss: () -> Unit,
    viewModel: ImportViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let {
            val lines = context.contentResolver.openInputStream(it)?.bufferedReader()?.readLines() ?: emptyList()
            viewModel.handleFileSelected(lines)
        }
    }

    if (uiState.importFinished) {
        CsAlertDialog(
            titleRes = localesR.string.import_finished,
            confirmButtonTextRes = localesR.string.ok,
            dismissButtonTextRes = localesR.string.cancel,
            icon = CsIcons.Outlined.Check,
            onConfirm = onDismiss,
            onDismiss = onDismiss,
        ) {
            Text(
                stringResource(
                    localesR.string.imported_transactions_count,
                    uiState.importedCount
                )
            )
        }
    } else {
        CsAlertDialog(
            titleRes = localesR.string.import_csv,
            confirmButtonTextRes = if (uiState.lines.isEmpty()) localesR.string.select_file else localesR.string.import_title,
            dismissButtonTextRes = localesR.string.cancel,
            icon = CsIcons.Outlined.ReceiptLong,
            onConfirm = {
                if (uiState.lines.isEmpty()) {
                    filePickerLauncher.launch(arrayOf("text/comma-separated-values", "text/csv"))
                } else {
                    viewModel.importTransactions()
                }
            },
            isConfirmEnabled = true,
            onDismiss = onDismiss,
        ) {
            if (uiState.isLoading) {
                LoadingState(modifier = Modifier.height(200.dp).fillMaxWidth())
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    if (uiState.lines.isNotEmpty()) {
                        Text(
                            text = stringResource(localesR.string.column_mapping),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        MappingField(
                            label = stringResource(localesR.string.date),
                            selectedIndex = uiState.config.dateColumnIndex,
                            columns = uiState.columns,
                            onColumnSelected = { viewModel.updateConfig(uiState.config.copy(dateColumnIndex = it)) }
                        )
                        MappingField(
                            label = stringResource(localesR.string.amount),
                            selectedIndex = uiState.config.amountColumnIndex,
                            columns = uiState.columns,
                            onColumnSelected = { viewModel.updateConfig(uiState.config.copy(amountColumnIndex = it)) }
                        )
                        MappingField(
                            label = stringResource(localesR.string.description),
                            selectedIndex = uiState.config.descriptionColumnIndex,
                            columns = uiState.columns,
                            onColumnSelected = { viewModel.updateConfig(uiState.config.copy(descriptionColumnIndex = it)) }
                        )

                        HorizontalDivider(Modifier.padding(vertical = 8.dp))

                        OutlinedTextField(
                            value = uiState.config.dateFormat,
                            onValueChange = { viewModel.updateConfig(uiState.config.copy(dateFormat = it)) },
                            label = { Text(stringResource(localesR.string.date_format)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = uiState.config.columnSeparator,
                            onValueChange = { viewModel.updateConfig(uiState.config.copy(columnSeparator = it)) },
                            label = { Text(stringResource(localesR.string.column_separator)) },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    } else {
                        Text(stringResource(localesR.string.select_csv_file_description))
                    }
                }
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
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
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
