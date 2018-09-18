package nl.amsterdam.openmonumentendag.data

import nl.amsterdam.openmonumentendag.monuments.data.PhotoJsonData

data class MonumentPhoto(val main: String, val thumb: String) {
    companion object {
        fun fromPhotoJsonData(photoJsonData: PhotoJsonData): MonumentPhoto {
            return MonumentPhoto(photoJsonData.main, photoJsonData.thumb)
        }
    }
}