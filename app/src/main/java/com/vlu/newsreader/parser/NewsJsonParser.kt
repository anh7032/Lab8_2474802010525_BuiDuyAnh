package com.vlu.newsreader.parser

import com.google.gson.Gson
import com.vlu.newsreader.model.NewsItem
import org.json.JSONArray
import org.json.JSONException

// Student Information
// Full Name: Bùi Duy Anh
// Student ID: 2474802010525

enum class ParsingStrategy {
    MANUAL,
    GSON
}

object NewsJsonParser {

    /**
     * Strategy 1: Manual Parsing using standard Android org.json (JSONArray & JSONObject).
     * Iterates through elements with getJSONObject(i) and maps each field manually.
     */
    @Throws(JSONException::class)
    fun parseManual(jsonString: String): List<NewsItem> {
        val newsList = ArrayList<NewsItem>()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val item = NewsItem(
                id = jsonObject.getInt("id"),
                userId = jsonObject.getInt("userId"),
                title = jsonObject.getString("title"),
                body = jsonObject.getString("body")
            )
            newsList.add(item)
        }
        return newsList
    }

    /**
     * Strategy 2: Automated Parsing using Google Gson library.
     * Reflection-based deserialization directly into Array<NewsItem>.
     */
    fun parseGson(jsonString: String): List<NewsItem> {
        return Gson().fromJson(jsonString, Array<NewsItem>::class.java).toList()
    }

    /**
     * Unified parse function that routes to the chosen ParsingStrategy.
     */
    fun parse(jsonString: String, strategy: ParsingStrategy): List<NewsItem> {
        return when (strategy) {
            ParsingStrategy.MANUAL -> parseManual(jsonString)
            ParsingStrategy.GSON -> parseGson(jsonString)
        }
    }
}
