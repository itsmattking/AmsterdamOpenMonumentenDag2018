package nl.amsterdam.openmonumentendag.source

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

open class ApiService<T>(baseUrl: String,
                         apiInterface: Class<T>,
                         moshi: Moshi,
                         okHttpClient: OkHttpClient = OkHttpClient.Builder().build()) {

    private val retrofitClient = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    val apiService: T = retrofitClient.create(apiInterface)

}