package com.example.netlibrary.data.remote

import com.example.netlibrary.domain.model.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PBApi(
    private val client: HttpClient,
    private val uploader: PBOkHttpUploader = PBOkHttpUploader()
) {

    private suspend inline fun <reified T> get(
        path: String,
        filter: String? = null
    ): T = client.get(path) {
        filter?.let { parameter("filter", it) }
    }.body()

    private suspend inline fun <reified T> post(
        path: String,
        data: Any
    ): T = client.post(path) {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()

    private suspend inline fun <reified T> patch(
        path: String,
        data: Any
    ): T = client.patch(path) {
        contentType(ContentType.Application.Json)
        setBody(data)
    }.body()

    suspend fun postUser(data: RequestRegister): ResponseRegister =
        post("collections/users/records", data)

    suspend fun getUser(id: String): User =
        get("collections/users/records/$id")

    suspend fun patchUser(id: String, data: RequestUser): User =
        patch("collections/users/records/$id", data)

    suspend fun authUser(data: RequestAuth): ResponseAuth =
        post("collections/users/auth-with-password", data)

    suspend fun getToken(): UsersAuth =
        get("collections/_authOrigins/records")

    suspend fun deleteToken(id: String) {
        client.delete("collections/_authOrigins/records/$id")
    }

    suspend fun getNews(): ResponsesNews =
        get("collections/news/records")

    suspend fun getProducts(filter: String?): ResponseProducts =
        get("collections/products/records", filter)

    suspend fun getProduct(id: String): Product =
        get("collections/products/records/$id")

    suspend fun getProject(): ResponsesProject =
        get("collections/project/records")

    suspend fun postProject(
        token: String,
        data: RequestProject
    ): Project = uploader.postProjectImg(token, data)

    suspend fun postBucket(data: RequestCart): ResponseCart =
        post("collections/cart/records", data)

    suspend fun patchBucket(
        id: String,
        data: RequestCart
    ): ResponseCart =
        patch("collections/cart/records/$id", data)

    suspend fun postOrder(data: RequestOrder): ResponseOrder =
        post("collections/orders/records", data)

    suspend fun getOrders(filter: String?): ResponseOrder =
        get("collections/orders/records", filter)
}