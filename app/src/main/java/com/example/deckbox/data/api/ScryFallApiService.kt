package com.example.deckbox.data.api

import com.example.deckbox.data.model.ScryfallSearchResponse
import retrofit2.Response
import retrofit2.http.*

interface ScryfallApiService {
    @GET("cards/search")
    suspend fun searchCards(
        @Query("q") query: String,
        @Query("unique") unique: String = "art"
    ): Response<ScryfallSearchResponse>
}