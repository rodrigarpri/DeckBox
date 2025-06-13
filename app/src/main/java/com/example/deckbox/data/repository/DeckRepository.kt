package com.example.deckbox.data.repository

import com.example.deckbox.data.api.ApiClient
import com.example.deckbox.data.model.Deck
import com.example.deckbox.data.model.DeckCreateRequest
import com.example.deckbox.data.model.DeckUpdateRequest

class DeckRepository {
    private val api = ApiClient.mtgApi

    suspend fun getDecks(nick: String): List<Deck> {
        return api.getDecks(nick).body() ?: emptyList()
    }

    suspend fun createDeck(deck: DeckCreateRequest): Int {
        val response = api.createDeck(deck = deck)

        if (response.isSuccessful) {
            return response.body()?.id ?: throw Exception("ID de mazo no recibido")
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMessage = when (response.code()) {
                409 -> "El mazo ya existe"
                else -> errorBody ?: "Error desconocido"
            }
            throw Exception(errorMessage)
        }
    }

    suspend fun updateDeck(deckId: Int, deckRequest: DeckUpdateRequest): Int {
        return api.updateDeck(id = deckId, deck = deckRequest).body() ?: throw Exception("Error al actualizar el mazo")
    }

    suspend fun deleteDeck(deckId: Int) {
        api.deleteDeck(id = deckId)
    }
}