package com.example.netlibrary.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object PBApiServis {

    const val BASE_URL = "http://10.0.2.2:8090/api/"

    var token: String? = null

    val instance: PBApi by lazy {
        PBApi(
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                            encodeDefaults = true
                        }
                    )
                }

                install(Logging) {
                    level = LogLevel.ALL
                }

                defaultRequest {
                    url(BASE_URL)
                    contentType(ContentType.Application.Json)
                    token?.let { bearerAuth(it) }
                }
            }
        )
    }
}