package ru.resodostudios.cashsense.feature.category.detail.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.common.CsDispatchers.Default
import ru.resodostudios.cashsense.core.common.Dispatcher
import ru.resodostudios.cashsense.core.common.di.ApplicationScope
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.groupByDate
import ru.resodostudios.cashsense.feature.category.detail.api.CategoryNavKey
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@HiltViewModel(assistedFactory = CategoryViewModel.Factory::class)
internal class CategoryViewModel @AssistedInject constructor(
    @Assisted private val key: CategoryNavKey,
    @ApplicationScope private val appScope: CoroutineScope,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
    private val categoriesRepository: CategoriesRepository,
    getExtendedUserWallets: GetExtendedUserWalletsUseCase,
) : ViewModel() {

    val categoryUiState: StateFlow<CategoryUiState> = combine(
        categoriesRepository.getCategory(key.categoryId),
        getExtendedUserWallets.invoke(),
    ) { category, wallets ->
        val transactions = wallets
            .flatMap { it.transactions }
            .sortedByDescending { it.timestamp }
            .filter { it.category?.id == category.id }
            .groupByDate()
        CategoryUiState.Success(
            category = category,
            groupedTransactions = transactions,
        )
    }
        .flowOn(defaultDispatcher)
        .catch {}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = CategoryUiState.Loading,
        )

    fun deleteCategory(id: String) {
        appScope.launch {
            categoriesRepository.deleteCategory(id)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: CategoryNavKey): CategoryViewModel
    }
}

internal sealed interface CategoryUiState {

    data object Loading : CategoryUiState

    data class Success(
        val category: Category,
        val groupedTransactions: Map<Instant, List<Transaction>>,
    ) : CategoryUiState
}