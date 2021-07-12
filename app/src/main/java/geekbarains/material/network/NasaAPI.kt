package com.lessons.nasa.network

import com.lessons.nasa.model.Apod
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaAPI {
    @GET("planetary/apod")
    fun getPictureOfTheDay(@Query("date") date: String): Observable<Apod>
}