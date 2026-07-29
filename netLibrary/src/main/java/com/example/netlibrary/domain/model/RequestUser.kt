package com.example.netlibrary.domain.model

import androidx.transition.Visibility

data class RequestUser(
    val email: String,
    val emailVisibility: Boolean,
    val firstname: String,
    val lastname: String,
    val secondname: String,
    val datebirthday: String,
    val gender: String
)