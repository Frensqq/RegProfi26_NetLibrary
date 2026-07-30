package com.example.netlibrary.domain.model

import kotlinx.serialization.Serializable

@Serializable

data class ProductItem(
    val id: String,
    val title: String,
    val price:Int,
    val typeCloses: String,
    val type: String
)