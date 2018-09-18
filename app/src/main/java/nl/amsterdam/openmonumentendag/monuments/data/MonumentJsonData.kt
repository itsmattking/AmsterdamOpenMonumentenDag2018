package nl.amsterdam.openmonumentendag.monuments.data

data class MonumentJsonData(val id: Int,
                            val title: String,
                            val address: String,
                            val times: List<OpeningTimesJsonData>,
                            val entry: String,
                            val accessible: Boolean,
                            val refreshments: Boolean,
                            val year: String,
                            val architect: String,
                            val description: String,
                            val fact: String,
                            val activities: List<String>,
                            val sights: String,
                            val event: String,
                            val link: String,
                            val photos: List<PhotoJsonData>,
                            val location: LocationJsonData)
