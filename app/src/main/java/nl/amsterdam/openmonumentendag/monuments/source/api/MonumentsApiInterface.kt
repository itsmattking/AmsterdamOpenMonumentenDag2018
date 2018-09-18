package nl.amsterdam.openmonumentendag.monuments.source.api

import nl.amsterdam.openmonumentendag.data.Monument
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MonumentsApiInterface {
    @GET("/monuments-{languageCode}.json")
    fun getMonuments(@Path("languageCode") languageCode: String = "en"): Call<List<Monument>>
}
