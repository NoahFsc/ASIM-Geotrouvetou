package fr.miage.geotrouvetou.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.miage.geotrouvetou.App
import fr.miage.geotrouvetou.domain.models.User
import fr.miage.geotrouvetou.utils.PasswordValidation
import fr.miage.geotrouvetou.utils.UserFieldValidator
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToMain: Boolean = false,
    val termsAccepted: Boolean = false,
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val supabase get() = getApplication<App>().supabase
    private val databaseService get() = getApplication<App>().databaseService

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(email: String, password: String, confirmPassword: String, nom: String, prenom: String) {
        val error = validateEmail(email) ?: validatePassword(password)
            ?: validateConfirmPassword(password, confirmPassword)
        if (error != null) {
            _uiState.value = _uiState.value.copy(error = error)
            return
        }
        if (!_uiState.value.termsAccepted) {
            _uiState.value = _uiState.value.copy(error = "Vous devez accepter les conditions d'utilisation")
            return
        }

        val fullName = "$prenom $nom".trim()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    data = buildJsonObject { put("full_name", fullName) }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = translateError(e.message))
                return@launch
            }

            try {
                supabase.auth.currentUserOrNull()?.let { user ->
                    databaseService.createProfile(
                        User(id = user.id, email = email, fullName = fullName)
                    )
                }
            } catch (_: Exception) { }

            _uiState.value = _uiState.value.copy(isLoading = false, navigateToMain = true)
        }
    }

    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigateToMain = false)
    }

    fun onTermsAcceptedChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(termsAccepted = value)
    }

    fun validateEmail(email: String): String? = UserFieldValidator.validateEmail(email)

    fun validatePassword(password: String): String? {
        if (password.isBlank()) return "Le mot de passe est requis"
        return PasswordValidation.of(password).firstError()
    }

    fun validateConfirmPassword(password: String, confirm: String): String? =
        PasswordValidation.confirmError(password, confirm)

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
