package com.lessons.nasa.model

sealed class ApodData {
    data class Success(val serverResponseData: Apod) : ApodData()
    data class Error(val error: Throwable) : ApodData()
    data class Loading(val progress: Int?) : ApodData()
}
