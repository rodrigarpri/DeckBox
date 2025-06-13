package com.example.deckbox.data.model

data class DeckUpdateRequest(
    val id: Int,
    val name: String,
    val image: String,
    val format: String,
    val wins: Int,
    val losses: Int,
    val user: String
)