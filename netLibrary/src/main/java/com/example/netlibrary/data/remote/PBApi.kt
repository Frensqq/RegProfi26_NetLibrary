package com.example.netlibrary.data.remote

import android.net.http.HttpEngine
import io.ktor.client.HttpClient

class PBApi(
    private val client: HttpClient,
    private val baseUrl: String
){

    private fun buildUrl(path: String): String = "$baseUrl$path"


}