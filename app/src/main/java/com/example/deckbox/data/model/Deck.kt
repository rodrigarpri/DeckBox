package com.example.deckbox.data.model

data class Deck(
    val id: Int,
    val name: String,
    val image: String,
    val format: String,
    val wins: Int,
    val losses: Int,
    val user: String
) {
    val winRate: Float
        get() = if (wins + losses == 0) 0f else wins.toFloat() / (wins + losses) * 100
}

data class DeckCreateRequest(
    val name: String,
    val image: String,
    val format: String,
    val wins: Int,
    val losses: Int,
    val user: String
)