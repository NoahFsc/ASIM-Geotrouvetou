package fr.miage.geoevent.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.miage.geoevent.GeoEventApplication
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToLogin: Boolean = false,
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val supabase get() = getApplication<GeoEventApplication>().supabase
    private val databaseService get() = getApplication<GeoEventApplication>().databaseService

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    _uiState.value = _uiState.value.copy(email = status.session.user?.email ?: "")
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                supabase.auth.signOut()
                _uiState.value = ProfileUiState(navigateToLogin = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Erreur lors de la déconnexion")
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Session expirée, reconnectez-vous")
                return@launch
            }
            try {
                databaseService.deleteProfile(userId)
                supabase.auth.signOut()
                _uiState.value = ProfileUiState(navigateToLogin = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Erreur lors de la suppression : ${e.message}")
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigateToLogin = false)
    }
}
