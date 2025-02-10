package com.bmc.buenacocina.domain.model

import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.common.SearchableTypes
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

data class ProductSearchDomain(
    override val id: String,
    val name: String,
    val category: ProductSearchCategoryDomain,
    val description: String,
    val discount: ProductSearchDiscountDomain,
    val rating: Float,
    val totalReviews: BigInteger,
    val store: ProductSearchStoreDomain,
    override val type: SearchableTypes,
    val image: String
) : Searchable {
    data class ProductSearchCategoryDomain(
        val name: String
    )

    data class ProductSearchDiscountDomain(
        val percentage: BigDecimal,
        val startDate: LocalDateTime,
        val endDate: LocalDateTime
    )

    data class ProductSearchStoreDomain(
        val name: String
    )
}
