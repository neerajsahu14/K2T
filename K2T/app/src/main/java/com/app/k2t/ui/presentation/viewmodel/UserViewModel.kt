package com.app.k2t.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.k2t.firebase.auth.FirebaseAuthManager
import com.app.k2t.firebase.model.User
import com.app.k2t.firebase.repositoryimpl.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class NavigationEvent {
    object NavigateToRoleRouter : NavigationEvent()
}

class UserViewModel : ViewModel(), KoinComponent {
    private val authManager: FirebaseAuthManager by inject()
    private val userRepository: UserRepositoryImpl by inject()

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            val firebaseUser = authManager.getCurrentFirebaseUser()
            if (firebaseUser != null) {
                _userState.value = userRepository.getUser(firebaseUser.uid)
            }
            _isLoading.value = false
        }
    }

    fun loginWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            authManager.loginWithEmailPassword(
                email,
                password,
                onSuccess = { firebaseUser, userDetails ->
                    _isLoading.value = false
                    if (firebaseUser != null && userDetails != null) {
                        _userState.value = userDetails
                        viewModelScope.launch {
                            _navigationEvents.emit(NavigationEvent.NavigateToRoleRouter)
                        }
                    } else {
                        _authError.value = "Login failed. User details not found."
                    }
                },
                onFailure = { error ->
                    _isLoading.value = false
                    _authError.value = error
                }
            )
        }
    }

    fun registerUser(email: String, password: String, userDetails: User, onNavigate: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            authManager.registerUser(
                email,
                password,
                userDetails,
                onSuccess = {
                    _isLoading.value = false
                    onNavigate("login")
                },
                onFailure = { error ->
                    _isLoading.value = false
                    _authError.value = error
                }
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authManager.signOut()
            _userState.value = null
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }
}

