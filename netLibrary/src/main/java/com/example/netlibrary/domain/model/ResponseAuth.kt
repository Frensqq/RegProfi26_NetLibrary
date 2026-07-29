package com.example.netlibrary.domain.model

data class ResponseAuth(
    val record: User,
    val token: String,
)