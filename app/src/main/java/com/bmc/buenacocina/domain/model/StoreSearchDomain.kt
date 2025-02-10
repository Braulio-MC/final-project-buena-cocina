package com.bmc.buenacocina.domain.model

import com.bmc.buenacocina.common.Searchable
import com.bmc.buenacocina.common.SearchableTypes
import java.math.BigInteger

data class StoreSearchDomain(
    override val id: String,
    val name: String,
    val description: String,
    val rating: Float,
    val totalReviews: BigInteger,
    override val type: SearchableTypes,
    val image: String
) : Searchable
