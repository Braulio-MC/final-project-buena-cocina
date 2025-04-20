package com.bmc.buenacocina.domain.model

import java.math.BigDecimal

data class InsightTopRatedStoreDomain(
    val id: String,
    val name: String,
    val image: String,
    val rating: BigDecimal,
    val totalReviews: Int
)
