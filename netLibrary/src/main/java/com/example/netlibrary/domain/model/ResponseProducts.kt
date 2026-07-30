package com.example.netlibrary.domain.model

import kotlinx.serialization.Serializable

@Serializable

data class ResponseProducts(
    val page: Int,
    val perPages:Int,
    val totalPage:Int,
    val totalItems:Int,
    val items: List<ProductItem>
)