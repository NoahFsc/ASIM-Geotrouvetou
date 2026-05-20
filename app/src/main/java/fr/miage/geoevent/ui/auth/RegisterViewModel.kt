package fr.miage.geoevent.ui.auth

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.miage.geoevent.GeoEventApplication
import fr.miage.geoevent.domain.models.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToMain: Boolean = false,
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val supabase get() = getApplication<GeoEventApplication>().supabase
    private val databaseService get() = getApplication<GeoEventApplication>().databaseService

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(email: String, password: String, confirmPassword: String) {
        val error = validateEmail(email) ?: validatePassword(password)
            ?: validateConfirmPassword(password, confirmPassword)
        if (error != null) {
            _uiState.value = _uiState.value.copy(error = error)
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            try {
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
            } catch (e: Exception) {
                _uiState.value = RegisterUiState(error = translateError(e.message))
                return@launch
            }

            try {
                supabase.auth.currentUserOrNull()?.let { user ->
                    databaseService.createProfile(User(id = user.id, email = email))
                }
            } catch (_: Exception) { }

            _uiState.value = RegisterUiState(navigateToMain = true)
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

    fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Le mot de passe est requis"
        password.length < 6 -> "Le mot de passe doit contenir au moins 6 caractères"
        else -> null
    }

    fun validateConfirmPassword(password: String, confirm: String): String? =
        if (password.isNotBlank() && confirm != password) "Les mots de passe ne correspondent pas" else null

    private fun translateError(message: String?): String = when {
        message == null -> "Une erreur inattendue s'est produite"
        message.contains("User already registered", ignoreCase = true) ->
            "Un compte existe déjà avec cette adresse email"
        message.contains("Password should be at least", ignoreCase = true) ->
            "Le mot de passe doit contenir au moins 6 caractères"
        message.contains("rate limit", ignoreCase = true) ||
        message.contains("too many requests", ignoreCase = true) ->
            "Trop de tentatives. Réessayez dans quelques minutes."
        message.contains("network", ignoreCase = true) ->
            "Erreur de connexion réseau. Vérifiez votre connexion internet."
        else -> "Une erreur est survenue. Réessayez."
    }
}
