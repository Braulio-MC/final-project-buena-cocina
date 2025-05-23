package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bmc.buenacocina.core.NetworkStatus
import com.bmc.buenacocina.core.SHARING_COROUTINE_TIMEOUT_IN_SEC
import com.bmc.buenacocina.data.network.dto.CreateStoreFavoriteDto
import com.bmc.buenacocina.domain.repository.ConnectivityRepository
import com.bmc.buenacocina.domain.repository.ProductRepository
import com.bmc.buenacocina.domain.repository.StoreFavoriteRepository
import com.bmc.buenacocina.domain.repository.StoreRepository
import com.bmc.buenacocina.domain.repository.UserRepository
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.model.StoreDomain
import com.bmc.buenacocina.domain.model.StoreFavoriteDomain
import com.bmc.buenacocina.domain.repository.StoreReviewRepository
import com.bmc.buenacocina.ui.screen.detailed.store.DetailedStoreIntent
import com.bmc.buenacocina.ui.screen.detailed.store.DetailedStoreUiState
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

@HiltViewModel(assistedFactory = DetailedStoreViewModel.DetailedStoreViewModelFactory::class)
class DetailedStoreViewModel @AssistedInject constructor(
    storeRepository: StoreRepository,
    private val storeFavoriteRepository: StoreFavoriteRepository,
    private val userRepository: UserRepository,
    storeReviewRepository: StoreReviewRepository,
    productRepository: ProductRepository,
    connectivityRepository: ConnectivityRepository,
    @Assisted private val storeId: String
) : ViewModel() {
    private val _events = Channel<DetailedStoreEvent>()
    val events = _events.receiveAsFlow()
    private val _store = storeRepository.get(storeId)
    private val _favorite = getStoreFavorite()
    private val _uiState = MutableStateFlow(DetailedStoreUiState())
    val uiState: StateFlow<DetailedStoreUiState> = _uiState
        .onStart {
            _store
                .onStart {
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingStore = true)
                    }
                }
                .onEach { store ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoadingStore = false,
                            store = store
                        )
                    }
                }
                .launchIn(viewModelScope)
            _favorite
                .onStart {
                    _uiState.update { currentState ->
                        currentState.copy(isLoadingFavorite = true)
                    }
                }
                .onEach { favorite ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoadingFavorite = false,
                            favorite = favorite.firstOrNull()
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = DetailedStoreUiState()
        )
    private val _qProducts: (Query) -> Query = { query ->
        query.whereEqualTo(FieldPath.of("store", "id"), storeId)
    }
    val products = productRepository
        .paging(_qProducts)
        .cachedIn(viewModelScope)
    val reviews = storeReviewRepository
        .pagingAnalyzedByStoreIdWithRange(storeId = storeId, limit = 3)
        .cachedIn(viewModelScope)
    val netState = connectivityRepository.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SHARING_COROUTINE_TIMEOUT_IN_SEC),
            initialValue = NetworkStatus.Unavailable
        )
    @OptIn(ExperimentalCoroutinesApi::class)
    val isStoreOpenFlow: Flow<Boolean> = uiState
        .map { it.store }
        .distinctUntilChanged()
        .filterNotNull()
        .flatMapLatest { store ->
            flow {
                emit(calculateIsStoreOpen(store))
                while (true) {
                    delay(60_000)
                    emit(calculateIsStoreOpen(store))
                }
            }
        }

    fun onIntent(intent: DetailedStoreIntent) {
        when (intent) {
            is DetailedStoreIntent.ToggleFavoriteStore -> {
                toggleFavoriteStore()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getStoreFavorite(): Flow<List<StoreFavoriteDomain>> = flow {
        emit(userRepository.getUserId())
    }.flatMapLatest { result ->
        when (result) {
            is Result.Error -> {
                flowOf(emptyList())
            }

            is Result.Success -> {
                val q: (Query) -> Query = { query ->
                    query.where(
                        Filter.and(
                            Filter.equalTo("userId", result.data),
                            Filter.equalTo("storeId", storeId)
                        )
                    )
                }
                storeFavoriteRepository.get(q)
            }
        }
    }

    private fun toggleFavoriteStore() {
        val favorite = uiState.value.favorite
        if (favorite != null) {
            deleteFavoriteStore()
        } else {
            createFavoriteStore()
        }
    }

    private fun createFavoriteStore() {
        _uiState.update { currentState ->
            currentState.copy(isWaitingForFavoriteResult = true)
        }
        viewModelScope.launch {
            uiState.value.store?.let { store ->
                when (val result = userRepository.getUserId()) {
                    is Result.Error -> {

                    }

                    is Result.Success -> {
                        val dto = CreateStoreFavoriteDto(
                            name = store.name,
                            description = store.description,
                            image = store.image,
                            phoneNumber = store.phoneNumber,
                            email = store.email,
                            storeId = store.id,
                            userId = result.data
                        )
                        storeFavoriteRepository.create(
                            dto,
                            onSuccess = {
                                processCreateFavoriteSuccess()
                            },
                            onFailure = { e ->
                                processCreateFavoriteFailed(e)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun processCreateFavoriteSuccess() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isWaitingForFavoriteResult = false)
            }
            _events.send(DetailedStoreEvent.CreateStoreFavoriteSuccess)
        }
    }

    private fun processCreateFavoriteFailed(e: Exception) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isWaitingForFavoriteResult = false)
            }
            _events.send(DetailedStoreEvent.CreateStoreFavoriteFailed(e))
        }
    }

    private fun deleteFavoriteStore() {
        uiState.value.favorite?.let { favorite ->
            _uiState.update { currentState ->
                currentState.copy(isWaitingForFavoriteResult = true)
            }
            storeFavoriteRepository.delete(
                favorite.id,
                onSuccess = {
                    processDeleteFavoriteSuccess()
                },
                onFailure = { e ->
                    processDeleteFavoriteFailed(e)
                }
            )
        }
    }

    private fun processDeleteFavoriteSuccess() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isWaitingForFavoriteResult = false)
            }
            _events.send(DetailedStoreEvent.DeleteStoreFavoriteSuccess)
        }
    }

    private fun processDeleteFavoriteFailed(e: Exception) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isWaitingForFavoriteResult = false)
            }
            _events.send(DetailedStoreEvent.DeleteStoreFavoriteFailed(e))
        }
    }

    private fun calculateIsStoreOpen(store: StoreDomain): Boolean {
        val now = LocalTime.now()
        val openingTime = LocalTime.of(store.startTime.hour, store.startTime.minute)
        val closingTime = LocalTime.of(store.endTime.hour, store.endTime.minute)
        return now.isAfter(openingTime) && now.isBefore(closingTime)
    }

    @AssistedFactory
    interface DetailedStoreViewModelFactory {
        fun create(storeId: String): DetailedStoreViewModel
    }

    sealed class DetailedStoreEvent {
        data object CreateStoreFavoriteSuccess : DetailedStoreEvent()
        data class CreateStoreFavoriteFailed(val error: Exception) : DetailedStoreEvent()
        data object DeleteStoreFavoriteSuccess : DetailedStoreEvent()
        data class DeleteStoreFavoriteFailed(val error: Exception) : DetailedStoreEvent()
    }
}