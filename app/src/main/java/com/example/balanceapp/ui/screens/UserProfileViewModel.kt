package com.example.balanceapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanceapp.data.UserProfile
import com.example.balanceapp.data.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserProfileState(
    val loading: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null
)

class UserProfileViewModel(
    private val repository: UserProfileRepository = UserProfileRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    fun loadProfile() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val profile = repository.getProfile()
                _state.value = UserProfileState(loading = false, profile = profile)
            } catch (t: Throwable) {
                _state.value = UserProfileState(
                    loading = false,
                    error = t.message ?: "Error al cargar el perfil"
                )
            }
        }
    }

    fun saveTeam(team: String, onSuccess: () -> Unit) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                repository.saveTeam(team)
                // Volvemos a cargar el perfil para reflejar el nuevo team
                val profile = repository.getProfile()
                _state.value = UserProfileState(loading = false, profile = profile)
                onSuccess()
            } catch (t: Throwable) {
                _state.value = UserProfileState(
                    loading = false,
                    error = t.message ?: "Error al guardar el team"
                )
            }
        }
    }
}



