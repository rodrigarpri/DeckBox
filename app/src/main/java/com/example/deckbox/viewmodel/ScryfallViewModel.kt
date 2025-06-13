package com.example.deckbox.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deckbox.data.api.ApiClient
import com.example.deckbox.data.model.ScryfallCard
import kotlinx.coroutines.launch

class ScryfallViewModel : ViewModel() {
    private val api = ApiClient.scryfallApi
    private val _searchResults = mutableStateOf<List<ScryfallCard>>(emptyList())
    val searchResults: State<List<ScryfallCard>> = _searchResults

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching

    fun searchCards(query: String) {
        if (query.length < 3) return

        viewModelScope.launch {
            _isSearching.value = true
            try {
                val response = api.searchCards(query)
                _searchResults.value = response.body()?.data ?: emptyList()
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }

    }

    fun resetSearch() {
        _searchResults.value = emptyList()
        _isSearching.value = false
    }

    private val _formats = mutableStateOf<List<String>>(emptyList())
    val formats: State<List<String>> = _formats

    init {
        loadFormats()
    }

    private fun loadFormats() {
        viewModelScope.launch {
            try {
                val response = api.searchCards("t:basic unique:art")
                val cards = response.body()?.data

                cards?.firstOrNull()?.legalities?.keys?.let { keys ->
                    val validFormats = keys.filterNot { it == "penny" || it == "duel" }
                    _formats.value = validFormats.map {
                        it.replaceFirstChar { char -> char.uppercaseChar() }
                    }.sorted()
                } ?: run {
                    setDefaultFormats()
                }
            } catch (e: Exception) {
                setDefaultFormats()
            }
        }
    }

    private fun setDefaultFormats() {
        _formats.value = listOf(
            "Standard", "Modern", "Commander", "Pioneer",
            "Legacy", "Vintage", "Pauper", "Historic", "Brawl", "Alchemy"
        ).sorted()
    }

    fun getCardImage(card: ScryfallCard): String? {
        return when {
            card.imageUris?.normal != null -> card.imageUris.normal

            card.cardFaces?.firstOrNull()?.imageUris?.normal != null ->
                card.cardFaces.first().imageUris!!.normal

            card.layout in listOf("transform", "modal_dfc", "double_faced_token") &&
                    card.cardFaces?.isNotEmpty() == true ->
                card.cardFaces.first().imageUris?.normal

            else -> null
        }
    }

    fun isDoubleFaced(card: ScryfallCard): Boolean {
        return card.layout in listOf(
            "transform", "modal_dfc", "double_faced_token", "art_series", "reversible_card"
        )
    }

}