package com.app.k2t.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.k2t.firebase.auth.FirebaseAuthManager
import com.app.k2t.firebase.model.User
import com.app.k2t.firebase.repositoryimpl.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserViewModel : ViewModel(), KoinComponent {
    private val authManager: FirebaseAuthManager by inject()
    private val userRepository: UserRepositoryImpl by inject()

    private val _userState = MutableStateFlow<User?>(null) // Represents the logged-in user's data from Firestore
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // _user is used for fetching specific user details, maybe not needed if userState covers it
    // For now, let's keep it if it serves a different purpose, or clarify if it can be merged with userState
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        val firebaseUser = authManager.getCurrentFirebaseUser()
        if (firebaseUser != null) {
            viewModelScope.launch {
                val userDetails = userRepository.getUser(firebaseUser.uid)
                _userState.value = userDetails
                _user.value = userDetails
            }
        }
    }

    fun loginWithEmailPassword(email: String, password: String, onNavigate: (String) -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            // Do not check user existence in Firestore by email, just use FirebaseAuth
            authManager.loginWithEmailPassword(
                email,
                password,
                onSuccess = { firebaseUser, userDetails ->
                    if (firebaseUser != null && userDetails != null) {
                        _userState.value = userDetails
                        onNavigate("dashboard")
                    } else {
                        _authError.value = "Login failed. Please check your credentials."
                    }
                },
                onFailure = { error ->
                    _authError.value = error // Show actual FirebaseAuth error
                }
            )
        }
    }

    fun getUser(userId: String) {
        viewModelScope.launch {
            val userResult = userRepository.getUser(userId)
            _user.value = userResult // For specific fetches
            // If this is the logged-in user, you might want to update _userState as well
            // if (authManager.getCurrentUserId() == userId) {
            // _userState.value = userResult
            // }
        }
    }

    fun registerUser(username: String, password: String, userDetails: User, onNavigate: (String) -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            authManager.registerUser(
                username, // username can be email or tableNumber
                password,
                userDetails,
                onSuccess = {
                    onNavigate("login")
                },
                onFailure = { error ->
                    _authError.value = error
                }
            )
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun checkCurrentUserState(onNavigate: (String) -> Unit) {
        viewModelScope.launch {
            val firebaseUser = authManager.getCurrentFirebaseUser()
            if (firebaseUser != null) {
                val userDetails = userRepository.getUser(firebaseUser.uid)
                if (userDetails != null) {
                    _userState.value = userDetails
                    onNavigate("dashboard")
                } else {
                    // User authenticated but no data in Firestore, prompt to complete profile
                    // This could be a route like "complete_profile_screen"
                    onNavigate("register_details") // A screen to fill in User model details
                }
            } else {
                onNavigate("login") // No user, go to login
            }
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch {
            authManager.signOut()
            _userState.value = null
            onSignedOut()
        }
    }
}
