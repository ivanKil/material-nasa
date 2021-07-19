package com.lessons.nasa.model

import com.google.gson.annotations.SerializedName

class Apod(
    @field:SerializedName("copyright") val copyright: String?,
    @field:SerializedName("date") val date: String?,
    @field:SerializedName("explanation") val explanation: String?,
    @field:SerializedName("media_type") val mediaType: String?,
    @field:SerializedName("title") val title: String?,
    @field:SerializedName("url") val url: String?,
    @field:SerializedName("hdurl") val hdurl: String?
)

//class EpicList(val list: Epic)

class Epic(
    @field:SerializedName("image") val image: String,
    val date: String
)

class MarsList(val photos: List<Mars>)
class Mars(val img_src: String)