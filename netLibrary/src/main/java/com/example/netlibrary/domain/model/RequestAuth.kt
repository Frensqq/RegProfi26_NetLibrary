package com.example.netlibrary.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RequestAuth(
    val identity: String,
    val password: String
)