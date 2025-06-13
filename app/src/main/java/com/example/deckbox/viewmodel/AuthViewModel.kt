package com.example.deckbox.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deckbox.data.model.LoginResponse
import com.example.deckbox.data.repository.UserRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class AuthViewModel : ViewModel() {
    private val repository = UserRepository()
    private val _user = mutableStateOf<LoginResponse?>(null)
    val user: State<LoginResponse?> = _user

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun login(nick: String, password: String) {
        viewModelScope.launch {
            try {
                _user.value = repository.login(nick, password)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun register(nick: String, name: String, password: String) {
        viewModelScope.launch {
            try {
                _user.value = repository.register(nick, name, password)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun logout() {
        _user.value = null
        _error.value = null
    }
}