package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.FormError
import com.bmc.buenacocina.domain.model.PaymentMethodDomain
import javax.inject.Inject

class ValidateShoppingCartPaymentMethod @Inject constructor() {
    operator fun invoke(paymentMethod: PaymentMethodDomain?): Result<Unit, FormError.ShoppingCartPaymentMethodError> {
        if (paymentMethod == null) {
            return Result.Error(FormError.ShoppingCartPaymentMethodError.NOT_SELECTED)
        }
        return Result.Success(Unit)
    }
}