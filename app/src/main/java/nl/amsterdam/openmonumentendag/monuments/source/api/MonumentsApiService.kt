package nl.amsterdam.openmonumentendag.monuments.source.api

import nl.amsterdam.openmonumentendag.data.Monument
import nl.amsterdam.openmonumentendag.monuments.data.MonumentJsonDataAdapter
import nl.amsterdam.openmonumentendag.source.ApiService
import android.support.annotation.WorkerThread
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import java.io.IOException


class MonumentsApiService(baseUrl: String = "http://localhost/",
                          okHttpClient: OkHttpClient,
                          val languageCode: String = "en") : ApiService<MonumentsApiInterface>(
        baseUrl,
        MonumentsApiInterface::class.java,
        Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(MonumentJsonDataAdapter())
                .build(),
        okHttpClient) {

    @WorkerThread
    fun getMonuments(): List<Monument> {
        return try {
            apiService.getMonuments(languageCode).execute().body() as List<Monument>
        } catch(e: IOException) {
            emptyList()
        } catch(e: kotlin.TypeCastException) { // when .execute().body() returns null :-/
            emptyList()
        }
    }
}
