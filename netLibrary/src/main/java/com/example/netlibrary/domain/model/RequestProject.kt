package com.example.netlibrary.domain.model

data class RequestProject (
    val title: String,
    val typeProject: String,
    val user_id: String,
    val dateStart: String,
    val dateEnd: String,
    val gender: String,
    val description_source: String,
    val category: String,
    val image: String
)