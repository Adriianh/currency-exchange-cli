package com.github.adriianh.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okio.Closeable
import okio.IOException

class HttpClient : Closeable {
    private val client = OkHttpClient()

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

    override fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
        client.cache?.close()
    }
}