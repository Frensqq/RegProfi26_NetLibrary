package com.example.netlibrary.domain.model

data class RequestAuth(
    val identity: String,
    val password: String,
)