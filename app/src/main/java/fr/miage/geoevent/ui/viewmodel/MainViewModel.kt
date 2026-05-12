package fr.miage.geoevent.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.miage.geoevent.data.backend.SupabaseDatabaseService
import fr.miage.geoevent.data.supabase
import fr.miage.geoevent.domain.interfaces.IDatabaseService
import fr.miage.geoevent.domain.models.GeoEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val databaseService: IDatabaseService = SupabaseDatabaseService(supabase)

    private val _events = MutableStateFlow<List<GeoEvent>>(emptyList())
    val events: StateFlow<List<GeoEvent>> = _events

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            try {
                databaseService.listenToEventsRealtime().collect { newEvents ->
                    _events.value = newEvents
                }
            } catch (e: Exception) {
                // Gérer l'erreur (ex: log)
            }
        }
    }
}
