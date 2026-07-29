package com.example.netlibrary.domain.model

data class RequestRegister (
    val email: String,
    val password: String,
    val passwordConfirm: String,
)