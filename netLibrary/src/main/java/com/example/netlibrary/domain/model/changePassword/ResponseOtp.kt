package com.example.netlibrary.domain.model.changePassword

import kotlinx.serialization.Serializable


@Serializable
data class ResponseOtp(
    val otpId: String
)