package com.example.deckbox.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deckbox.data.model.Deck
import com.example.deckbox.data.model.DeckCreateRequest
import com.example.deckbox.data.model.DeckUpdateRequest
import com.example.deckbox.data.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeckViewModel(private val repository: DeckRepository = DeckRepository()) : ViewModel() {


    private val _decks = mutableStateOf<List<Deck>>(emptyList())
    val decks: State<List<Deck>> = _decks

    private val _currentDeck = MutableStateFlow<Deck?>(null)
    val currentDeck: StateFlow<Deck?> = _currentDeck.asStateFlow()

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _hasInitialAnimationShown = MutableStateFlow(false)
    val hasInitialAnimationShown: StateFlow<Boolean> = _hasInitialAnimationShown

    fun setInitialAnimationShown(shown: Boolean) {
        _hasInitialAnimationShown.value = shown
    }

    fun loadDecks(nick: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _decks.value = repository.getDecks(nick)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadDeckById(deckId: Int, userNick: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (_decks.value.isEmpty()) {
                    loadDecks(userNick)
                }
                val deck = _decks.value.find { it.id == deckId }
                _currentDeck.value = deck
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
                _currentDeck.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun createDeck(deckRequest: DeckCreateRequest) {
        repository.createDeck(deckRequest)
        loadDecks(deckRequest.user)
    }

    fun updateDeck(deckId: Int, updatedDeckData: Deck) {
        viewModelScope.launch {

            val deckUpdateRequest = DeckUpdateRequest(
                id = updatedDeckData.id,
                name = updatedDeckData.name,
                image = updatedDeckData.image,
                format = updatedDeckData.format,
                wins = updatedDeckData.wins,
                losses = updatedDeckData.losses,
                user = updatedDeckData.user
            )

            try {
                Log.d(
                    "DeckViewModel",
                    "Intentando actualizar mazo en el repositorio. DeckId: $deckId, Wins: ${updatedDeckData.wins}"
                )
                val affectedRows = repository.updateDeck(deckId, deckUpdateRequest)

                if (affectedRows > 0) {
                    // Actualizar el mazo actual en el ViewModel
                    _currentDeck.value = updatedDeckData

                    // Actualizar la lista de mazos
                    _decks.value = _decks.value.map { deck ->
                        if (deck.id == deckId) updatedDeckData else deck
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }


    private val _createDeckError = mutableStateOf<String?>(null)


    fun deleteDeck(deckId: Int) {
        _isLoading.value = true
        _createDeckError.value = null

        viewModelScope.launch {
            try {
                repository.deleteDeck(deckId)

                _currentDeck.value = null

                _decks.value = _decks.value.filter { it.id != deckId }
                _error.value = null

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }


}