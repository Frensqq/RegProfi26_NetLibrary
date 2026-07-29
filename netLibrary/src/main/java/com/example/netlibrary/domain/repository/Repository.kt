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
import com.example.netlibrary.domain.model.ResponseOrder
import com.example.netlibrary.domain.model.ResponseProducts
import com.example.netlibrary.domain.model.ResponseRegister
import com.example.netlibrary.domain.model.ResponsesNews
import com.example.netlibrary.domain.model.ResponsesProject
import com.example.netlibrary.domain.model.User
import com.example.netlibrary.domain.model.UsersAuth


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

    suspend fun postProject(data: RequestProject): NetworkResult<Project>

    suspend fun postBucket(data: RequestCart): NetworkResult<ResponseCart>

    suspend fun patchBucket(id:String, data: RequestCart): NetworkResult<ResponseCart>
    suspend fun postOrder(data: RequestOrder): NetworkResult<ResponseOrder>

    suspend fun getOrders(filter: String? = null): NetworkResult<ResponseOrder>

}