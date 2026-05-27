package com.example.digifin.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.digifin.data.local.SessionManager
import com.example.digifin.data.model.User
import com.example.digifin.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository()
    private val sessionManager = SessionManager(application)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()

    init {
        fetchUserData()
    }

    fun fetchUserData() {
        viewModelScope.launch {
            val user = repository.getUserData()
            _userData.value = user
            user?.firstName?.let {
                Log.d("AuthViewModel", "Saving user name to session: $it")
                sessionManager.saveUserName(it)
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, pass)
            if (result.isSuccess) {
                fetchUserData() // Fetch and "cookie" the name after success
                _authState.value = AuthState.Success(result.getOrNull())
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(
        email: String,
        pass: String,
        firstName: String,
        lastName: String,
        country: String,
        currency: String
    ) {
        Log.d("AuthViewModel", "Register clicked for $email")
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(email, pass, firstName, lastName, country, currency)
            if (result.isSuccess) {
                Log.d("AuthViewModel", "Register success")
                fetchUserData() // Also fetch after registration
                _authState.value = AuthState.Success(result.getOrNull())
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Registration failed"
                Log.e("AuthViewModel", "Register error: $errorMsg")
                _authState.value = AuthState.Error(errorMsg)
            }
        }
    }

    fun logout() {
        repository.logout()
        sessionManager.clearSession()
        _userData.value = null
        _authState.value = AuthState.Idle
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.resetPassword(email)
            if (result.isSuccess) {
                _authState.value = AuthState.Idle // Or a specific success state
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Reset failed")
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}
