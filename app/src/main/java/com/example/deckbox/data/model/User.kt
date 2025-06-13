package com.example.deckbox.data.model

data class User(
    val nick: String,
    val name: String,
    val password: String
)

data class LoginResponse(
    val nick: String,
    val name: String
)