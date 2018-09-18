package nl.amsterdam.openmonumentendag.data

import nl.amsterdam.openmonumentendag.monuments.data.MonumentJsonData

data class Monument(val id: Int,
                    val title: String,
                    val address: String,
                    val times: List<MonumentOpeningTime>,
                    val entry: String,
                    val accessible: Boolean,
                    val refreshments: Boolean,
                    val year: String,
                    val architect: String,
                    val description: String,
                    val fact: String,
                    val sights: String,
                    val activityInfos: List<String>,
                    val event: String,
                    val link: String,
                    val photos: List<MonumentPhoto>,
                    val location: MonumentLocation,
                    var saved: Boolean,
                    val fetchedTimestamp: Long) {

    companion object {

        val EMPTY = Monument(-1,
            "EMPTY",
            "",
            emptyList(),
            "",
            false,
            false,
            "",
            "",
            "",
            "",
            "",
            emptyList(),
            "",
            "",
            emptyList(),
            MonumentLocation(-1.0, -1.0),
            false,
            System.currentTimeMillis())

        fun fromMonumentJsonData(monumentJsonData: MonumentJsonData): Monument {
            return Monument(
                monumentJsonData.id,
                monumentJsonData.title,
                monumentJsonData.address,
                monumentJsonData.times.map {
                    MonumentOpeningTime.fromOpeningTimesJsonData(it)
                },
                monumentJsonData.entry,
                monumentJsonData.accessible,
                monumentJsonData.refreshments,
                monumentJsonData.year,
                monumentJsonData.architect,
                monumentJsonData.description,
                monumentJsonData.fact,
                monumentJsonData.sights,
                monumentJsonData.activities,
                monumentJsonData.event,
                monumentJsonData.link,
                monumentJsonData.photos.map {
                    MonumentPhoto.fromPhotoJsonData(it)
                },
                MonumentLocation.fromLocationJsonData(monumentJsonData.location),
                false,
                System.currentTimeMillis()
            )
        }
    }
}