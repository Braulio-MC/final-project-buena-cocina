package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.FormError
import com.bmc.buenacocina.domain.model.ShoppingCartItemDomain
import javax.inject.Inject

class ValidateShoppingCartItems @Inject constructor() {
    operator fun invoke(items: List<ShoppingCartItemDomain>): Result<Unit, FormError.ShoppingCartItemsError> {
        if (items.isEmpty()) {
            return Result.Error(FormError.ShoppingCartItemsError.ITEMS_ARE_EMPTY)
        }
        return Result.Success(Unit)
    }
}