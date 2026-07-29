package com.example.netlibrary.domain.model

data class ResponsesProject(
    val page:Int,
    val perPage:Int,
    val totalPages:Int,
    val totalItems:Int,
    val items:List<Project>,
)