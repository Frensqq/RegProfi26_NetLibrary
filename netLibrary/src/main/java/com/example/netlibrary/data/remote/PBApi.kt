package com.example.netlibrary.data.remote

import android.net.http.HttpEngine
import com.example.netlibrary.domain.model.Product
import com.example.netlibrary.domain.model.Project
import com.example.netlibrary.domain.model.RequestAuth
import com.example.netlibrary.domain.model.RequestCart
import com.example.netlibrary.domain.model.RequestOrder
import com.example.netlibrary.domain.model.RequestProject
import com.example.netlibrary.domain.model.RequestRegister
import com.example.netlibrary.domain.model.RequestUser
import com.example.netlibrary.domain.model.ResponseAuth
import com.example.netlibrary.domain.model.ResponseCart
import com.example.netlibrary.domain.model.ResponseOrder
import com.example.netlibrary.domain.model.ResponseProducts
import com.example.netlibrary.domain.model.ResponseRegister
import com.example.netlibrary.domain.model.ResponsesNews
import com.example.netlibrary.domain.model.ResponsesProject
import com.example.netlibrary.domain.model.User
import com.example.netlibrary.domain.model.UsersAuth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.collections.mapOf

class PBApi(
    private val client: HttpClient,
    private val baseUrl: String
){

    private fun buildUrl(path: String): String = "$baseUrl$path"

    suspend fun postUser(data: RequestRegister): ResponseRegister {
        return client.post(buildUrl("collections/users/records")){
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
    }

    suspend fun getUser(id: String): User {
        return client.get(buildUrl("collections/users/records/$id")).body()
    }

    suspend fun patchUser(id: String, data: RequestUser):User{
        return client.patch(buildUrl("collections/users/records/$id")){
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
    }

    suspend fun authUser(data: RequestAuth): ResponseAuth{
        return client.post(buildUrl("collections/users/auth-with-password")){
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
    }

    suspend fun getToken(): UsersAuth{
        return client.get(buildUrl("collections/_authOrigins/records")).body()
    }

    suspend fun deleteToken(id: String){
        client.delete(buildUrl("collections/_authOrigins/records/$id"))
    }

    suspend fun getNews(): ResponsesNews{
        return client.get(
            buildUrl("collections/news/records")
        ).body()
    }

    suspend fun getProducts(filter: String? = null): ResponseProducts{
        return client.get(
            buildUrl("collections/news/records")){
                filter?.let { parameter("filter", it) }
        }.body()
    }

    suspend fun getProduct(id: String): Product{
        return client.get(
            buildUrl("collections/products/records/$id")
        ).body()
    }

    suspend fun getProject(): ResponsesProject{
        return client.get(
            buildUrl("collections/project/records")
        ).body()
    }

    suspend fun postProject(data: RequestProject): Project{
        return client.post(
            buildUrl("collections/project/records")){
                contentType(ContentType.Application.Json)
                setBody(data)
            }.body()
    }

    suspend fun postBucket(data: RequestCart): ResponseCart{
        return client.post(
            buildUrl("collections/cart/records")){
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
    }

    suspend fun patchBucket(id:String, data: RequestCart): ResponseCart{
        return  client.patch(
            buildUrl("collections/cart/records/$id")){
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
    }

    suspend fun postOrder(data: RequestOrder): ResponseOrder{
        return client.post(
            buildUrl("collections/orders/records")){
            contentType(ContentType.Application.Json)
            setBody(data)
        }.body()
    }

    suspend fun getOrders(filter: String? = null): ResponseOrder{
        return client.get(
            buildUrl("collections/orders/records")){
            filter?.let { parameter("filter", it) }
        }.body()
    }
}