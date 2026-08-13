package com.example.netlibrary.domain.model.changePassword

import kotlinx.serialization.Serializable

@Serializable
data class RequestChangePass(

    val password: String,
    val passwordConfirm: String
)