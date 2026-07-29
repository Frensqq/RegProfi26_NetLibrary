package com.example.netlibrary.domain.model

data class Error400(
    val status: Int,
    val message: String,
    val data: Map<String, Any> = emptyMap()
)