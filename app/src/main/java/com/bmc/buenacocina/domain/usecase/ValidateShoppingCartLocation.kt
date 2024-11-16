package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.FormError
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class ValidateShoppingCartLocation @Inject constructor() {
    operator fun invoke(location: LatLng?): Result<Unit, FormError.ShoppingCartLocationError> {
        if (location == null) {
            return Result.Error(FormError.ShoppingCartLocationError.NOT_SELECTED)
        }
        return Result.Success(Unit)
    }
}