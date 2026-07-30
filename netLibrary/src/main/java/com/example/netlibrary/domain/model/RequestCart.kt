package com.example.netlibrary.domain.model

import kotlinx.serialization.Serializable

@Serializable

data class RequestCart (
    val user_id: String,
    val product_id: String,
    val count: Int,
)