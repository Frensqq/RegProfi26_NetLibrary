package com.example.netlibrary.domain.model

import kotlinx.serialization.Serializable

@Serializable

data class ResponseOrder(
    val id: String,
    val collectionId: String,
    val collectionName: String,
    val created: String,
    val updated: String,
    val user_id: String,
    val product_id: String,
    val count: Int
)