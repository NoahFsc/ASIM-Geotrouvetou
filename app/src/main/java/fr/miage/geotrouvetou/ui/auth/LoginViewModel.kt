package fr.miage.geotrouvetou.ui.auth

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.miage.geotrouvetou.App
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToMain: Boolean = false,
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val supabase get() = getApplication<App>().supabase

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (supabase.auth.currentSessionOrNull() != null) {
                _uiState.value = LoginUiState(navigateToMain = true)
            }
        }
    }

    fun login(email: String, password: String) {
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        if (emailError != null || passwordError != null) {
            _uiState.value = _uiState.value.copy(error = emailError ?: passwordError)
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                _uiState.value = LoginUiState(navigateToMain = true)
            } catch (e: Exception) {
                _uiState.value = LoginUiState(error = translateError(e.message))
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigateToMain = false)
    }

    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "L'adresse email est requise"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format d'email invalide"
        else -> null
    }

    fun validatePassword(password: String): String? =
        if (password.isBlank()) "Le mot de passe est requis" else null

    private fun translateError(message: String?): String = when {
        message == null -> "Une erreur inattendue s'est produite"
        message.contains("Invalid login credentials", ignoreCase = true) ->
            "Email ou mot de passe incorrect"
        message.contains("Email not confirmed", ignoreCase = true) ->
            "Veuillez confirmer votre email avant de vous connecter"
        message.contains("rate limit", ignoreCase = true) ||
        message.contains("too many requests", ignoreCase = true) ->
            "Trop de tentatives. Réessayez dans quelques minutes."
        message.contains("network", ignoreCase = true) ||
        message.contains("Unable to connect", ignoreCase = true) ->
            "Erreur de connexion réseau. Vérifiez votre connexion internet."
        else -> "Une erreur est survenue. Réessayez."
    }
}
