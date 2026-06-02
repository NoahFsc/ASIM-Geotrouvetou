package fr.miage.geotrouvetou.ui.params

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import fr.miage.geotrouvetou.App
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ParamsUiState(
    val isLoading: Boolean = false,
    val isAdmin: Boolean = false,
    val isSigningOut: Boolean = false,
    val error: String? = null,
)

class ParamViewModel(application: Application) : AndroidViewModel(application) {

    private val supabase get() = getApplication<App>().supabase
    private val databaseService get() = getApplication<App>().databaseService

    private val _uiState = MutableStateFlow(ParamsUiState())
    val uiState: StateFlow<ParamsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return@launch
            val profile = runCatching { databaseService.getProfile(userId) }.getOrNull()
            _uiState.value = _uiState.value.copy(isAdmin = profile?.role == "admin")
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                supabase.auth.signOut()
                _uiState.value = _uiState.value.copy(navigateToLogin = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Erreur lors de la déconnexion")
            }
        }
    }
}
