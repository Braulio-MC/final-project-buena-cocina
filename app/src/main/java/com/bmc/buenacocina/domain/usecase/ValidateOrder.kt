package com.bmc.buenacocina.domain.usecase

import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.error.FormError
import com.bmc.buenacocina.domain.model.OrderDomain
import javax.inject.Inject

class ValidateOrder @Inject constructor() {
    operator fun invoke(order: OrderDomain?): Result<Unit, FormError.OrderError> {
        if (order == null) {
            return Result.Error(FormError.OrderError.NOT_VALID)
        }
        return Result.Success(Unit)
    }
}