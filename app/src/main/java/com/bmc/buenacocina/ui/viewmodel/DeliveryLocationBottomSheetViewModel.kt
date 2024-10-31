package com.bmc.buenacocina.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bmc.buenacocina.domain.repository.LocationRepository
import com.google.firebase.firestore.Query
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = DeliveryLocationBottomSheetViewModel.DeliveryLocationBottomSheetViewModelFactory::class)
class DeliveryLocationBottomSheetViewModel @AssistedInject constructor(
    locationRepository: LocationRepository,
    @Assisted private val storeId: String
) : ViewModel() {
    private val _qLocation: (Query) -> Query = { query ->
        query.whereEqualTo("storeId", storeId)
    }
    val locations = locationRepository
        .paging(_qLocation)
        .cachedIn(viewModelScope)

    @AssistedFactory
    interface DeliveryLocationBottomSheetViewModelFactory {
        fun create(storeId: String): DeliveryLocationBottomSheetViewModel
    }
}