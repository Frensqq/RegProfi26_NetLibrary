package com.example.netlibrary.domain.model

import kotlinx.serialization.Serializable
import java.io.File


data class RequestProject (
    val title: String,
    val typeProject: String,
    val user_id: String,
    val dateStart: String,
    val dateEnd: String,
    val gender: String,
    val description_source: String,
    val category: String,
    val image: File? = null
)