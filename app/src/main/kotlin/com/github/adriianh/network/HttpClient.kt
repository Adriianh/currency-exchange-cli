package com.github.adriianh.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okio.Closeable
import okio.IOException

/**
 * A simple HTTP client for making GET requests using OkHttp.
 */
class HttpClient : Closeable {
    private val client = OkHttpClient()

    /**
     * Makes a GET request to the specified URL and returns the response body as a string.
     *
     * @param url The URL to make the GET request to.
     * @return The response body as a string.
     * @throws IOException If the request fails or the response is empty.
     */
    suspend fun get(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Request failed with status: ${response.code}")
            }
            response.body?.string() ?: throw IOException("Empty response")
        }
    }

    /**
     * Closes the HTTP client, releasing any resources held by it.
     */
    override fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
        client.cache?.close()
    }
}