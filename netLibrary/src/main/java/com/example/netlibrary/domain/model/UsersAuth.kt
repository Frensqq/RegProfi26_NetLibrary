package com.example.netlibrary.domain.model

data class UsersAuth(
    val page:Int,
    val perPage:Int,
    val totalPages: Int,
    val totalItems:Int,
    val item: List<UserAuth>
)