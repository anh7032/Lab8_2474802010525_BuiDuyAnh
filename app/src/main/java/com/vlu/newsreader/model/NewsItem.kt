package com.vlu.newsreader.model

import com.google.gson.annotations.SerializedName

// Student Information
// Full Name: Bùi Duy Anh
// Student ID: 2474802010525

data class NewsItem(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)
