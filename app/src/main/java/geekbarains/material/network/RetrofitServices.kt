package com.lessons.nasa.network


import geekbarains.nasa.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class RetrofitServices {
    companion object Factory {
        const val API_KEY: String = BuildConfig.NASA_API_KEY
        const val BASE_URL = "https://api.nasa.gov/"
        private var retrofit: NasaAPI? = null
        fun create(): NasaAPI {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(createOkHttpClient(ApiInterceptor()))
                    .baseUrl(BASE_URL).build()
                    .create(NasaAPI::class.java)
            }
            return retrofit!!
        }

        private fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(interceptor)
            httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            return httpClient.build()
        }

        class ApiInterceptor : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val request = chain.request()
                val url = request.url().newBuilder()
                    .addQueryParameter("api_key", API_KEY)
                    .build()
                val newRequest = request.newBuilder().url(url).build()
                return chain.proceed(newRequest)
            }
        }

    }

}