package nl.amsterdam.openmonumentendag.data

import nl.amsterdam.openmonumentendag.monuments.data.LocationJsonData

data class MonumentLocation(val latitude: Double, val longitude: Double) {
    companion object {
        fun fromLocationJsonData(locationJsonData: LocationJsonData): MonumentLocation {
            return MonumentLocation(locationJsonData.latitude, locationJsonData.longitude)
        }
    }
}