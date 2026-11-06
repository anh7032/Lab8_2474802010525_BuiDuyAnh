package com.vlu.newsreader.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

// Student Information
// Full Name: Bùi Duy Anh
// Student ID: 2474802010525

object HttpClient {

    private const val CONNECT_TIMEOUT_MS = 15000
    private const val READ_TIMEOUT_MS = 15000

    /**
     * Executes asynchronous HTTP GET request using Kotlin Coroutines on Dispatchers.IO.
     * Guarantees that network I/O never blocks the Android UI Main thread.
     */
    suspend fun get(urlString: String): String = withContext(Dispatchers.IO) {
        getInternal(urlString)
    }

    /**
     * Executes synchronous HTTP GET directly on the caller thread.
     * Used specifically in Step 6 (Intentional Debugging Challenge) to reproduce NetworkOnMainThreadException.
     */
    fun getSyncRaw(urlString: String): String {
        return getInternal(urlString)
    }

    private fun getInternal(urlString: String): String {
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null
        try {
            val url = URL(urlString)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("Accept", "application/json")
                doInput = true
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))
                val responseBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    responseBuilder.append(line)
                }
                return responseBuilder.toString()
            } else {
                throw IOException("HTTP Request failed with Response Code: $responseCode")
            }
        } finally {
            try {
                reader?.close()
            } catch (_: IOException) {}
            connection?.disconnect()
        }
    }
}
