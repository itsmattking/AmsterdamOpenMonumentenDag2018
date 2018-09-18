package nl.amsterdam.openmonumentendag

import android.support.multidex.MultiDexApplication
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import nl.amsterdam.openmonumentendag.monuments.source.db.MonumentDbOpenHelper
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


class OpenMonumentenDagApplication : MultiDexApplication() {
    companion object {
        lateinit var okHttpClient: OkHttpClient
        lateinit var monumentDbHelper: MonumentDbOpenHelper
    }

    override fun onCreate() {
        super.onCreate()
        okHttpClient = buildOkHttpClient()
        monumentDbHelper = buildMonumentDbHelper()
    }

    fun buildOkHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .cache(Cache(cacheDir, 3L * 1024 * 1024))
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        return okHttpClientBuilder.build()
    }

    fun buildMonumentDbHelper(): MonumentDbOpenHelper {
       return MonumentDbOpenHelper(this)
    }
}

fun Picasso.loadWithBaseUrl(url: String): RequestCreator {
    return load(String.format("http://localhost/%s", url))
}
