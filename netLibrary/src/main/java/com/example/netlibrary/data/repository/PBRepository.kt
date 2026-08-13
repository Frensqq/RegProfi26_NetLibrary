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
import com.example.netlibrary.domain.model.ResponseCarts
import com.example.netlibrary.domain.model.ResponseOrder
import com.example.netlibrary.domain.model.ResponseProducts
import com.example.netlibrary.domain.model.ResponseRegister
import com.example.netlibrary.domain.model.ResponsesNews
import com.example.netlibrary.domain.model.ResponsesProject
import com.example.netlibrary.domain.model.User
import com.example.netlibrary.domain.model.UsersAuth
import com.example.netlibrary.domain.model.changePassword.OTPAuthRequest
import com.example.netlibrary.domain.model.changePassword.OTPAuthResponse
import com.example.netlibrary.domain.model.changePassword.PasswordResetRequest
import com.example.netlibrary.domain.model.changePassword.RequestChangePass
import com.example.netlibrary.domain.model.changePassword.RequestOtp
import com.example.netlibrary.domain.model.changePassword.ResponseOtp
import com.example.netlibrary.domain.repository.Repository
import com.example.netlibrary.network.IsConnect
import com.example.netlibrary.network.NetworkMonitor
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import okio.IOException

class PBRepository(
    private val api: PBApi,
    private val networkMonitor: IsConnect,
    private val context: Context
): Repository {

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T>{
        if (!networkMonitor.isConnected()){
            return NetworkResult.NoInternet
        }

        return try {
            NetworkResult.Success(apiCall())
        } catch (e: IOException){
            NetworkResult.NoInternet
        } catch (e: ResponseException) {
            NetworkResult.Error(
                Error400(
                    status = e.response.status.value,
                    message = e.message.toString(),
                    data = mapOf("raw" to runCatching {
                        e.response.body<String>()
                    }.getOrDefault(""))
                )
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                Error400(
                    status = -1,
                    message = e.message ?: "Unknown error"
                )
            )
        }
    }

    override suspend fun authUser(data: RequestAuth) =
        safeApiCall { api.authUser(data) }

    override suspend fun deleteToken(id: String) {
        safeApiCall { api.deleteToken(id) }
    }

    override suspend fun getNews()= safeApiCall {
        api.getNews()
    }

    override suspend fun getOrders(filter: String?)= safeApiCall {
        api.getOrders(filter)
    }

    override suspend fun getProduct(id: String) = safeApiCall {
        api.getProduct(id)
    }

    override suspend fun getProducts(filter: String?) = safeApiCall {
        api.getProducts(filter)
    }

    override suspend fun getProject() = safeApiCall {
        api.getProject()
    }

    override suspend fun getToken() = safeApiCall {
        api.getToken()
    }

    override suspend fun getUser(id: String) = safeApiCall {
        api.getUser(id)
    }

    override suspend fun patchBucket(id: String, data: RequestCart) = safeApiCall {
        api.patchBucket(id,data)
    }

    override suspend fun patchUser(id: String, data: RequestUser) = safeApiCall {
        api.patchUser(id,data)
    }

    override suspend fun postBucket(data: RequestCart) = safeApiCall {
        api.postBucket(data)
    }

    override suspend fun postOrder(data: RequestOrder) = safeApiCall {
        api.postOrder(data)
    }

    override suspend fun postProject(token:String, data: RequestProject) = safeApiCall {
        api.postProject(token, data)
    }

    override suspend fun postUser(data: RequestRegister) = safeApiCall {
        api.postUser(data)
    }

    override fun getImageUrl(collection: String, id: String, image: String): String {
       return api.getImageUrl(collection,id,image)
    }

    override suspend fun getBucket(filter: String?) = safeApiCall{
        api.getBucket(filter)
    }

    override suspend fun deleteBucket(id: String?): NetworkResult<Unit> = safeApiCall{
        api.deleteBucket(id)
    }

    override suspend fun OtpAuth(data: OTPAuthRequest) = safeApiCall{
        api.OtpAuth(data)
    }

    override suspend fun OtpRequest(data: RequestOtp)= safeApiCall {
        api.OtpRequest(data)
    }

    override suspend fun ResetPass(data: PasswordResetRequest)= safeApiCall{
        api.ResetPass(data)
    }

    override suspend fun patchPass(id: String, data: RequestChangePass): NetworkResult<User> =safeApiCall{
        api.patchPass(id,data)
    }
}