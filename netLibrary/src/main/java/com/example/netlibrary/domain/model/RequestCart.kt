package com.example.netlibrary.domain.model

data class RequestCart (
    val user_id: String,
    val product_id: String,
    val count: Int,
)