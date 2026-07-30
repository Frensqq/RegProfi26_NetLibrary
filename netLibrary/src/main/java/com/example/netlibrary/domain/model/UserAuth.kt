package com.example.netlibrary.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAuth (
    val id: String,
    val collectionId: String,
    val collectionName: String,
    val created: String,
    val updated: String,
    val collectionRef: String,
    val fingerprint: String,
    val recordRef: String
)