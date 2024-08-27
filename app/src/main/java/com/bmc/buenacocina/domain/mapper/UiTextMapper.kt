package com.bmc.buenacocina.domain.mapper

import com.bmc.buenacocina.R
import com.bmc.buenacocina.domain.error.DataError
import com.bmc.buenacocina.domain.Result
import com.bmc.buenacocina.domain.UiText
import com.bmc.buenacocina.domain.error.FormError

object UiTextMapper {
    fun asUiText(dataError: DataError): UiText {
        return when (dataError) {
            DataError.LocalError.NOT_FOUND -> UiText.StringResource(
                R.string.ui_text_data_error_local_not_found
            )

            DataError.NetworkError.SERVER_UNAVAILABLE -> UiText.StringResource(
                R.string.ui_text_data_error_network_server_unavailable
            )

            DataError.NetworkError.NOT_FOUND -> UiText.StringResource(
                R.string.ui_text_data_error_network_not_found
            )

            DataError.NetworkError.BAD_REQUEST -> UiText.StringResource(
                R.string.ui_text_data_error_network_bad_request
            )

            DataError.NetworkError.UNAUTHORIZED -> UiText.StringResource(
                R.string.ui_text_data_error_network_unauthorized
            )

            DataError.NetworkError.UNPROCESSABLE_CONTENT -> UiText.StringResource(
                R.string.ui_text_data_error_network_unprocessable_content
            )

            DataError.NetworkError.INTERNAL_SERVER_ERROR -> UiText.StringResource(
                R.string.ui_text_data_error_network_internal_server_error
            )

            DataError.NetworkError.UNKNOWN -> UiText.StringResource(
                R.string.ui_text_data_error_network_unknown
            )

            DataError.LocalError.DELETION_FAILED -> UiText.StringResource(
                R.string.ui_text_data_error_local_deletion_failed
            )

            DataError.LocalError.CREATION_FAILED -> UiText.StringResource(
                R.string.ui_text_data_error_local_creation_failed
            )

            DataError.LocalError.RESOURCE_ALREADY_CREATED -> UiText.StringResource(
                R.string.ui_text_data_error_local_resource_already_created
            )

            DataError.LocalError.UPDATE_FAILED -> UiText.StringResource(R.string.ui_text_data_error_local_update_failed)
            DataError.LocalError.CART_STORE_MISMATCH -> UiText.StringResource(R.string.ui_text_data_error_local_cart_store_mismatch)
        }
    }

    fun asUiText(formError: FormError): UiText {
        return when (formError) {
            FormError.ShoppingCartLocationError.NOT_SELECTED -> UiText.StringResource(R.string.ui_text_form_error_shopping_cart_location_not_selected)
            FormError.ShoppingCartPaymentMethodError.NOT_SELECTED -> UiText.StringResource(R.string.ui_text_form_error_shopping_cart_payment_method_not_selected)
            FormError.ShoppingCartError.NOT_VALID -> UiText.StringResource(R.string.ui_text_form_error_shopping_cart_error_not_valid)
            FormError.ShoppingCartItemsError.ITEMS_ARE_EMPTY -> UiText.StringResource(R.string.ui_text_form_error_shopping_cart_items_are_empty)
        }
    }
}

fun DataError.asUiText() = UiTextMapper.asUiText(this)
fun Result.Error<*, DataError>.asDataErrorUiText(): UiText {
    return error.asUiText()
}

fun FormError.asUiText() = UiTextMapper.asUiText(this)
fun Result.Error<*, FormError>.asFormErrorUiText(): UiText {
    return error.asUiText()
}
