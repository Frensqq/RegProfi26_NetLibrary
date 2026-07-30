package com.example.netlibrary.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class News(
    val id: String,
    val collectionId: String,
    val collectionName: String,
    val created: String,
    val updated: String,
    val newsImage: String
)