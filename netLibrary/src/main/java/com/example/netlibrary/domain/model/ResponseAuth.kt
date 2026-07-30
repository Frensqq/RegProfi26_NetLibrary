package com.example.netlibrary.domain.model

import kotlinx.serialization.Serializable

@Serializable

data class ResponseAuth(
    val record: User,
    val token: String,
)