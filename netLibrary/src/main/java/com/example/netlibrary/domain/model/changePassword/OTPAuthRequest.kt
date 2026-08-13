package com.example.netlibrary.domain.model.changePassword

import kotlinx.serialization.Serializable

@Serializable
data class OTPAuthRequest (
    val otpId: String,
    val password: String
)