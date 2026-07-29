package com.example.netlibrary.domain.model

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T): NetworkResult<T>()
    data class Error(val errorResponse: Error400) : NetworkResult<Nothing>()
    object NoInternet: NetworkResult<Nothing>()
}