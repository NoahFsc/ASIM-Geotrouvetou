package fr.miage.geotrouvetou.ui.params

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import fr.miage.geotrouvetou.App
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ParamsUiState(
    val isSigningOut: Boolean = false,
    val error: String? = null,
)

class ParamViewModel(application: Application) : AndroidViewModel(application) {

    private val supabase get() = getApplication<App>().supabase

    private val _uiState = MutableStateFlow(ParamsUiState())
    val uiState: StateFlow<ParamsUiState> = _uiState.asStateFlow()

    suspend fun signOut(): Boolean {
        _uiState.value = _uiState.value.copy(isSigningOut = true, error = null)
        return try {
            supabase.auth.signOut()
            true
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isSigningOut = false, error = "Erreur lors de la déconnexion")
            false
        }
    }
}
