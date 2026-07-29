package com.example.netlibrary.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object PBApiServis {

    private const val BASE_URL = "http://10.0.2.2:8090/api/"

    private var token: String? = null

    fun setToken(newToken: String?){
        token = newToken
    }

    fun getToken(): String? = token

    val instance: PBApi by lazy {
        val client = HttpClient(OkHttp){
            install(ContentNegotiation){
                json(Json{
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }

            install(Logging){
                level = LogLevel.ALL
            }
            defaultRequest {
                url(BASE_URL)
                token?.let {
                    header(HttpHeaders.Authorization, "Bearer $it")
                }
            }
        }
        PBApi(client,BASE_URL)

    }

}