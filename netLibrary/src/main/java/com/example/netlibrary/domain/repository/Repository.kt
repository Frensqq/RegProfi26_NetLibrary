package com.example.netlibrary.domain.repository

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


interface Repository
{
    suspend fun postUser(data: RequestRegister): NetworkResult<ResponseRegister>

    suspend fun getUser(id: String): NetworkResult<User>

    suspend fun patchUser(id: String, data: RequestUser):NetworkResult<User>


    suspend fun authUser(data: RequestAuth): NetworkResult<ResponseAuth>

    suspend fun getToken(): NetworkResult<UsersAuth>

    suspend fun deleteToken(id: String)
    suspend fun getNews(): NetworkResult<ResponsesNews>

    suspend fun getProducts(filter: String? = null): NetworkResult<ResponseProducts>

    suspend fun getProduct(id: String): NetworkResult<Product>

    suspend fun getProject(): NetworkResult<ResponsesProject>

    suspend fun postProject(token:String, data: RequestProject): NetworkResult<Project>

    suspend fun postBucket(data: RequestCart): NetworkResult<ResponseCart>

    suspend fun patchBucket(id:String, data: RequestCart): NetworkResult<ResponseCart>
    suspend fun postOrder(data: RequestOrder): NetworkResult<ResponseOrder>

    suspend fun getOrders(filter: String? = null): NetworkResult<ResponseCarts>

    fun getImageUrl(collection: String, id: String,image: String ): String

    suspend fun getBucket(filter: String?): NetworkResult<ResponseCarts>

    suspend fun deleteBucket(id: String?) : NetworkResult<Unit>

    suspend fun OtpRequest(data: RequestOtp): NetworkResult<ResponseOtp>

    suspend fun OtpAuth(data: OTPAuthRequest): NetworkResult<OTPAuthResponse>

    suspend fun ResetPass(data: PasswordResetRequest): NetworkResult<Unit>

    suspend fun patchPass(id: String,data: RequestChangePass): NetworkResult<User>


}