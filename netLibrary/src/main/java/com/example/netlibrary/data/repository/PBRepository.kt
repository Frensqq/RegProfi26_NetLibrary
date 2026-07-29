package com.example.netlibrary.data.repository

import android.content.Context
import com.example.netlibrary.data.remote.PBApi
import com.example.netlibrary.domain.model.Error400
import com.example.netlibrary.domain.model.NetworkResult
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
import com.example.netlibrary.domain.repository.Repository
import com.example.netlibrary.network.NetworkMonitor
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import okio.IOException

class PBRepository(
    private val api: PBApi,
    private val networkMonitor: NetworkMonitor,
    private val context: Context
): Repository {

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T>{
        if (!networkMonitor.IsConnected()){
            return NetworkResult.NoInternet
        }

        return try {
            NetworkResult.Success(apiCall())
        } catch (e: IOException){
            NetworkResult.NoInternet
        } catch (e: ClientRequestException){
            NetworkResult.Error(
                Error400(
                    status = e.response.status.value,
                    message = e.message ?: "Client error",
                    data = mapOf("raw" to (e.response.body() ?: ""))
                )
            )
        } catch (e: ServerResponseException){
            NetworkResult.Error(
                Error400(
                    status = e.response.status.value,
                    message = e.message ?: "Client error",
                    data = mapOf("raw" to (e.response.body() ?: ""))
                )
            )
        } catch (e: Exception){
            NetworkResult.Error(
                Error400(
                    status = -1,
                    message = e.message ?: "Unknown error"
                )
            )
        }
    }

    override suspend fun authUser(data: RequestAuth): NetworkResult<ResponseAuth> =
        safeApiCall { api.authUser(data) }

    override suspend fun deleteToken(id: String) {
        safeApiCall { api.deleteToken(id) }
    }

    override suspend fun getNews(): NetworkResult<ResponsesNews> = safeApiCall {
        api.getNews()
    }

    override suspend fun getOrders(filter: String?): NetworkResult<ResponseOrder> = safeApiCall {
        api.getOrders(filter)
    }

    override suspend fun getProduct(id: String): NetworkResult<Product> = safeApiCall {
        api.getProduct(id)
    }

    override suspend fun getProducts(filter: String?): NetworkResult<ResponseProducts> = safeApiCall {
        api.getProducts(filter)
    }

    override suspend fun getProject(): NetworkResult<ResponsesProject> = safeApiCall {
        api.getProject()
    }

    override suspend fun getToken(): NetworkResult<UsersAuth> = safeApiCall {
        api.getToken()
    }

    override suspend fun getUser(id: String): NetworkResult<User> = safeApiCall {
        api.getUser(id)
    }

    override suspend fun patchBucket(id: String, data: RequestCart): NetworkResult<ResponseCart> = safeApiCall {
        api.patchBucket(id,data)
    }

    override suspend fun patchUser(id: String, data: RequestUser): NetworkResult<User> = safeApiCall {
        api.patchUser(id,data)
    }

    override suspend fun postBucket(data: RequestCart): NetworkResult<ResponseCart> = safeApiCall {
        api.postBucket(data)
    }

    override suspend fun postOrder(data: RequestOrder): NetworkResult<ResponseOrder> = safeApiCall {
        api.postOrder(data)
    }

    override suspend fun postProject(data: RequestProject): NetworkResult<Project> = safeApiCall {
        api.postProject(data)
    }

    override suspend fun postUser(data: RequestRegister): NetworkResult<ResponseRegister> = safeApiCall {
        api.postUser(data)
    }
}