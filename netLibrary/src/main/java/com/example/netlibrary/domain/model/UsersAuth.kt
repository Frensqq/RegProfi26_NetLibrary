package com.example.netlibrary.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UsersAuth(
    val page:Int,
    val perPage:Int,
    val totalPages: Int,
    val totalItems:Int,
    val item: List<UserAuth>
)