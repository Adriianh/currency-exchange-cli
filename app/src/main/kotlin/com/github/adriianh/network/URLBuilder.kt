package com.github.adriianh.network

import okhttp3.HttpUrl

/**
 * Object responsible for building URLs for the currency API.
 */
object URLBuilder {
    private const val BASE_SCHEME = "https"
    private const val BASE_HOST = "cdn.jsdelivr.net"
    private const val BASE_PATH = "npm/@fawazahmed0/currency-api"

    /**
     * Builds a URL with the given path segments appended to the base path.
     *
     * @param pathSegments The path segments to append to the base path.
     * @return The constructed HttpUrl.
     */
    fun buildUrl(vararg pathSegments: String): HttpUrl {
        val builder = HttpUrl.Builder()
            .scheme(BASE_SCHEME)
            .host(BASE_HOST)
            .addEncodedPathSegments("$BASE_PATH@latest/v1/")

        pathSegments.forEach { segment -> builder.addEncodedPathSegment(segment) }

        return builder.build()
    }

    /**
     * Builds a URL with a version prefix and the given path segments appended to the base path.
     *
     * @param versionPrefix The version prefix to use in the URL.
     * @param pathSegments The path segments to append to the base path.
     * @return The constructed HttpUrl.
     */
    fun buildUrlWithVersionPrefix(
        versionPrefix: String,
        vararg pathSegments: String
    ): HttpUrl {
        val builder = HttpUrl.Builder()
            .scheme(BASE_SCHEME)
            .host(BASE_HOST)
            .addEncodedPathSegments("$BASE_PATH@$versionPrefix")

        pathSegments.forEach { segment -> builder.addPathSegment(segment) }

        return builder.build()
    }
}