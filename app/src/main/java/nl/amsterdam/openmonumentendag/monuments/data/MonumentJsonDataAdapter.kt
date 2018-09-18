package nl.amsterdam.openmonumentendag.monuments.data

import nl.amsterdam.openmonumentendag.data.Monument
import com.squareup.moshi.FromJson

class MonumentJsonDataAdapter {
    @FromJson fun fromJson(data: MonumentJsonDataWrapper) : List<Monument> {
        return data.results.map {
            monumentJsonData -> Monument.fromMonumentJsonData(monumentJsonData)
        }
    }
}