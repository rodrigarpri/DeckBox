package com.example.deckbox.data.repository

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.deckbox.data.api.ApiClient
import com.example.deckbox.data.model.LoginResponse
import com.example.deckbox.data.model.User

class UserRepository {
    private val api = ApiClient.mtgApi

    suspend fun login(nick: String, password: String): LoginResponse {
        return api.login(nick, password).body() ?: throw Exception("Login fallido")
    }

    suspend fun register(nick: String, name: String, password: String): LoginResponse {
        val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        val user = User(nick, name, hashedPassword)
        return api.register(user).body() ?: throw Exception("Registro fallido")
    }
}