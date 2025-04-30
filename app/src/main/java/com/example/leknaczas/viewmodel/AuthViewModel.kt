package com.example.leknaczas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leknaczas.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    
    private val _isLoggedIn = MutableStateFlow(authRepository.isUserLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
    
    init {
        _isLoggedIn.value = authRepository.isUserLoggedIn()
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = {
                    _loginState.value = LoginState.Success
                    _isLoggedIn.value = true
                },
                onFailure = { exception ->
                    _loginState.value = LoginState.Error(exception.message ?: "Login failed")
                }
            )
        }
    }
    
    fun signup(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = authRepository.signup(email, password)
            result.fold(
                onSuccess = {
                    _loginState.value = LoginState.Success
                    _isLoggedIn.value = true
                },
                onFailure = { exception ->
                    _loginState.value = LoginState.Error(exception.message ?: "Sign up failed")
                }
            )
        }
    }
    
    fun logout() {
        authRepository.logout()
        _isLoggedIn.value = false
        _loginState.value = LoginState.Idle
    }
    
    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}