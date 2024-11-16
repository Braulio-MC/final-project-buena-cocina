package com.bmc.buenacocina.domain

class LocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Parece que has rechazado el permiso de la ubicacion permanentemente, puedes ir a los ajustes de la aplicacion y permitir el acceso a la ubicacion o puedes descartar este mensaje"
        } else {
            "El permiso de la ubicacion es importante para ver tu localizacion en el mapa y mejorar la experiencia, pero no es necesario"
        }
    }
}