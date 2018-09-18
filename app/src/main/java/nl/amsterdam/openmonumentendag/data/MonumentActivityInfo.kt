package nl.amsterdam.openmonumentendag.data

import nl.amsterdam.openmonumentendag.monuments.data.ActivityJsonData

data class MonumentActivityInfo(val marker: String,
                                val title: String,
                                val entry: String,
                                val time: String,
                                val start: String,
                                val link: String) {
    companion object {
        fun fromActivityJsonData(activityJsonData: ActivityJsonData): MonumentActivityInfo {
            return MonumentActivityInfo(
                    activityJsonData.marker,
                    activityJsonData.title,
                    activityJsonData.entry,
                    activityJsonData.time,
                    activityJsonData.start,
                    activityJsonData.link
            )
        }
    }
}