package com.github.adriianh.network

import okhttp3.HttpUrl

object URLBuilder {
    private const val BASE_SCHEME = "https"
    private const val BASE_HOST = "cdn.jsdelivr.net"
    private const val BASE_PATH = "npm/@fawazahmed0/currency-api"

    /**
     * Construye una URL básica añadiendo segmentos específicos del servicio.
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
     * Construye una URL específica con un prefijo dinámico, como una fecha.
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