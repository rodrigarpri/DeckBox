package com.example.deckbox.data.api

import com.example.deckbox.data.model.*
import retrofit2.Response
import retrofit2.http.*

data class DeckCreationResponse(val id: Int)

interface MtgApiService {
    @GET("userController.php")
    suspend fun login(
        @Query("nick") nick: String,
        @Query("password") password: String
    ): Response<LoginResponse>

    @POST("userController.php")
    suspend fun register(
        @Body user: User
    ): Response<LoginResponse>

    @GET("deckController.php")
    suspend fun getDecks(
        @Query("nick") nick: String
    ): Response<List<Deck>>

    @POST("deckController.php")
    suspend fun createDeck(
        @Query("accion") accion: String = "crear",
        @Body deck: DeckCreateRequest
    ): Response<DeckCreationResponse>

    @POST("deckController.php")
    suspend fun updateDeck(
        @Query("accion") accion: String = "actualizar",
        @Query("id") id: Int,
        @Body deck: DeckUpdateRequest
    ): Response<Int>

    @POST("deckController.php")
    suspend fun deleteDeck(
        @Query("accion") accion: String = "eliminar",
        @Query("id") id: Int
    ): Response<Unit>
}