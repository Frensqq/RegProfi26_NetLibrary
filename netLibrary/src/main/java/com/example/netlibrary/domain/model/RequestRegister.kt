package com.example.netlibrary.domain.model

import kotlinx.serialization.Serializable

@Serializable

data class RequestRegister (
    val email: String,
    val password: String,
    val passwordConfirm: String,
)