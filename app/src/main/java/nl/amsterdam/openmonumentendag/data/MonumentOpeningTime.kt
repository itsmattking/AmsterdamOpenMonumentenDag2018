package nl.amsterdam.openmonumentendag.data

import nl.amsterdam.openmonumentendag.monuments.data.OpeningTimesJsonData

data class MonumentOpeningTime(val date: String, val open: String, val close: String) {
    companion object {
        fun fromOpeningTimesJsonData(openingTimesJsonData: OpeningTimesJsonData): MonumentOpeningTime {
            return MonumentOpeningTime(openingTimesJsonData.date, openingTimesJsonData.open, openingTimesJsonData.close)
        }
    }
}