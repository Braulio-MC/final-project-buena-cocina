package com.bmc.buenacocina.domain.model

data class SearchResultDomain(
    val id: String,
    val name: String,
    val image: String,
    val type: String,
    val description1: String,
    val description2: String
)
