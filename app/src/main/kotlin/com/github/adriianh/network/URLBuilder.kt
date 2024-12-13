package com.github.adriianh.network

import okhttp3.HttpUrl

object URLBuilder {

    private const val BASE_SCHEME = "https"
    private const val BASE_HOST = "cdn.jsdelivr.net"
    private const val BASE_PATH = "npm/@fawazahmed0/currency-api@latest/v1"

    fun buildUrl(vararg pathSegments: String): HttpUrl {
        val builder = HttpUrl.Builder()
            .scheme(BASE_SCHEME)
            .host(BASE_HOST)
            .addEncodedPathSegments(BASE_PATH)

        pathSegments.forEach { segment -> builder.addEncodedPathSegment(segment) }

        return builder.build()
    }

    fun buildUrlWithParams(
        vararg pathSegments: String,
        queryParams: Map<String, String> = emptyMap()
    ): HttpUrl {
        val builder = HttpUrl.Builder()
            .scheme(BASE_SCHEME)
            .host(BASE_HOST)
            .addEncodedPathSegments(BASE_PATH)

        pathSegments.forEach { segment -> builder.addPathSegment(segment) }
        queryParams.forEach { (key, value) -> builder.addQueryParameter(key, value) }

        return builder.build()
    }
}