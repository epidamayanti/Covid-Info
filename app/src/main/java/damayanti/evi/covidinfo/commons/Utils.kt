package damayanti.evi.covidinfo.commons

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class Utils {
    companion object {

        //endpoint API
        val ENDPOINT = "https://api.covid19api.com/"
        const val SUMMARY = "summary"
        const val COUNTRIES = "countries"

        //

        //retrofit
        fun buildClient(): OkHttpClient.Builder {
            val clientBuilder = OkHttpClient.Builder()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            clientBuilder.addInterceptor(loggingInterceptor)
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)

            return clientBuilder
        }



    }
}