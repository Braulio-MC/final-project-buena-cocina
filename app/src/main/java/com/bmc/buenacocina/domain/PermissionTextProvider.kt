package com.bmc.buenacocina.domain

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}