package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.FormError
import com.bmc.buenacocina.domain.model.ShoppingCartDomain
import javax.inject.Inject

class ValidateShoppingCart @Inject constructor() {
    operator fun invoke(cart: ShoppingCartDomain?): Result<Unit, FormError.ShoppingCartError> {
        if (cart == null) {
            return Result.Error(FormError.ShoppingCartError.NOT_VALID)
        }
        return Result.Success(Unit)
    }
}