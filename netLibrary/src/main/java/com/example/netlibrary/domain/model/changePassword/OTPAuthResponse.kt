package com.example.netlibrary.domain.model.changePassword

import com.example.netlibrary.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class OTPAuthResponse (
    val token: String,
    val record: User
)
