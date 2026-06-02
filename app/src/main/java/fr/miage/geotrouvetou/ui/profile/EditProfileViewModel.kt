package fr.miage.geotrouvetou.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.miage.geotrouvetou.App
import fr.miage.geotrouvetou.utils.UserFieldValidator
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val nom: String = "",
    val prenom: String = "",
    val email: String = "",
    val originalNom: String = "",
    val originalPrenom: String = "",
    val avatarUrl: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isUploadingAvatar: Boolean = false,
    val saveToastKey: Int = 0,
    val avatarToastKey: Int = 0,
    val error: String? = null,
    val navigateToLogout: Boolean = false,
) {
    val hasChanges: Boolean get() = nom != originalNom || prenom != originalPrenom
    val formValid: Boolean get() = UserFieldValidator.isNomValid(nom) && UserFieldValidator.isPrenomValid(prenom)
}

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val supabase get() = getApplication<App>().supabase
    private val databaseService get() = getApplication<App>().databaseService

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    val userId = status.session.user?.id ?: return@collect
                    val email = status.session.user?.email ?: ""
                    loadProfile(userId, email)
                }
            }
        }
    }

    private suspend fun loadProfile(userId: String, email: String) {
        try {
            val profile = databaseService.getProfile(userId)
            val fullName = profile?.fullName ?: ""
            val parts = fullName.split(" ", limit = 2)
            val nom = UserFieldValidator.capitalizeFirst(parts.getOrNull(0) ?: "")
            val prenom = UserFieldValidator.capitalizeFirst(parts.getOrNull(1) ?: "")
            _uiState.value = _uiState.value.copy(
                nom = nom, prenom = prenom, email = email,
                originalNom = nom, originalPrenom = prenom,
                avatarUrl = profile?.avatarUrl,
                isLoading = false,
            )
        } catch (_: Exception) {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun onNomChange(value: String) {
        _uiState.value = _uiState.value.copy(
            nom = UserFieldValidator.capitalizeFirst(value),
            error = null,
        )
    }

    fun onPrenomChange(value: String) {
        _uiState.value = _uiState.value.copy(
            prenom = UserFieldValidator.capitalizeFirst(value),
            error = null,
        )
    }

    fun save() {
        val state = _uiState.value
        if (!state.hasChanges || !state.formValid) return
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            try {
                val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return@launch
                val fullName = "${state.nom} ${state.prenom}"
                databaseService.updateProfile(userId, fullName)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    originalNom = state.nom,
                    originalPrenom = state.prenom,
                    saveToastKey = _uiState.value.saveToastKey + 1,
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = "Erreur lors de la sauvegarde")
            }
        }
    }

    fun updateAvatar(bytes: ByteArray) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingAvatar = true, error = null)
            try {
                val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return@launch
                databaseService.updateAvatar(userId, bytes)
                val profile = databaseService.getProfile(userId)
                _uiState.value = _uiState.value.copy(
                    isUploadingAvatar = false,
                    avatarUrl = profile?.avatarUrl,
                    avatarToastKey = _uiState.value.avatarToastKey + 1,
                )
            } catch (e: Exception) {
                Log.e("EditProfile", "updateAvatar failed", e)
                _uiState.value = _uiState.value.copy(
                    isUploadingAvatar = false,
                    error = "Erreur lors de la mise à jour de la photo",
                )
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return@launch
                databaseService.deleteProfile(userId)
                supabase.auth.signOut()
                _uiState.value = _uiState.value.copy(navigateToLogout = true)
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(error = "Erreur lors de la suppression du compte")
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigateToLogout = false)
    }
}
