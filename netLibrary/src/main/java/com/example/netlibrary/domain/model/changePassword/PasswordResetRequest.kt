package com.example.netlibrary.domain.model.changePassword

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetRequest (
    val token: String,
    val password: String,
    val passwordConfirm: String
)