package com.example.netlibrary.domain.model

data class News(
    val id: String,
    val collectionId: String,
    val collectionName: String,
    val created: String,
    val updated: String,
    val newsImage: String
)