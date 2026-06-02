package fr.miage.geotrouvetou.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.miage.geotrouvetou.App
import fr.miage.geotrouvetou.domain.models.Evenement
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ProfileTab { MesEvenements, MesParticipations }

data class ProfileUiState(
    val email: String = "",
    val fullName: String = "",
    val avatarUrl: String? = null,
    val events: List<Evenement> = emptyList(),
    val selectedTab: ProfileTab = ProfileTab.MesEvenements,
    val isLoading: Boolean = true,
    val error: String? = null,
    val navigateToLogin: Boolean = false,
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val supabase get() = getApplication<App>().supabase
    private val databaseService get() = getApplication<App>().databaseService

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    val userId = status.session.user?.id ?: return@collect
                    _uiState.value = _uiState.value.copy(email = status.session.user?.email ?: "")
                    loadProfile(userId)
                    loadEvents(userId)
                }
            }
        }
    }

    private suspend fun loadProfile(userId: String) {
        try {
            val profile = databaseService.getProfile(userId)
            _uiState.value = _uiState.value.copy(
                fullName = profile?.fullName ?: "",
                avatarUrl = profile?.avatarUrl,
                isLoading = false,
            )
        } catch (_: Exception) {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private suspend fun loadEvents(userId: String) {
        try {
            val userEvents = databaseService.getAllEvents().filter { it.user_id == userId }
            _uiState.value = _uiState.value.copy(events = userEvents)
        } catch (_: Exception) { }
    }

    fun onTabSelected(tab: ProfileTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun onNavigationHandled() {
        _uiState.value = _uiState.value.copy(navigateToLogin = false)
    }
}
