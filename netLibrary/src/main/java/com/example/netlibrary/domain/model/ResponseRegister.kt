package com.example.netlibrary.domain.model

data class ResponseRegister (
    val id: String,
    val collectionId: String,
    val collectionName: String,
    val created: String,
    val updated: String,
    val emailVisibility: Boolean,
    val firstname: String,
    val lastname: String,
    val secondname: String,
    val verified: Boolean,
    val datebirthday: String,
    val gender: String,
)