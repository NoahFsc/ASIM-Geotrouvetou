package fr.miage.geotrouvetou.ui.events

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.miage.geotrouvetou.domain.interfaces.IDatabaseService
import fr.miage.geotrouvetou.domain.models.Evenement
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val databaseService: IDatabaseService,
    private val supabase: SupabaseClient
) : ViewModel() {

    var event by mutableStateOf<Evenement?>(null)
    
    var isLoading by mutableStateOf(false)
        private set

    var isJoined by mutableStateOf(false)
        private set

    var isOwner by mutableStateOf(false)
        private set

    var participantsCount by mutableIntStateOf(0)
        private set

    var joinToastKey by mutableIntStateOf(0)
        private set

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                event = databaseService.getEvent(eventId)
                
                val user = supabase.auth.currentUserOrNull()
                if (user != null) {
                    isJoined = databaseService.isUserParticipating(eventId, user.id)
                    isOwner = event?.user_id == user.id
                }
                participantsCount = databaseService.getParticipantsCount(eventId)
            } catch (e: Exception) {
                // Gérer l'erreur
            } finally {
                isLoading = false
            }
        }
    }

    fun joinEvent() {
        val currentEvent = event ?: return
        val eventId = currentEvent.id ?: return
        
        viewModelScope.launch {
            try {
                val user = supabase.auth.currentUserOrNull()
                if (user != null) {
                    databaseService.joinEvent(eventId, user.id)
                    isJoined = true
                    participantsCount++
                    joinToastKey++
                }
            } catch (e: Exception) {
                // Gérer l'erreur (ex: déjà inscrit)
            }
        }
    }
}
