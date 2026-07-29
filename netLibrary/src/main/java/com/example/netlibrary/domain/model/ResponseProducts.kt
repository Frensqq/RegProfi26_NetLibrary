package com.example.netlibrary.domain.model

data class ResponseProducts(
    val page: Int,
    val perPage:Int,
    val totalPage:Int,
    val totalItems:Int,
    val items: List<ProductItem>
)