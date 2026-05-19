package fr.miage.geoevent.ui.events

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.miage.geoevent.domain.interfaces.IDatabaseService
import fr.miage.geoevent.domain.models.GeoEvent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CreateEventViewModel(
    private val databaseService: IDatabaseService,
    private val supabase: SupabaseClient
) : ViewModel() {

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)
    var isLoading by mutableStateOf(false)
        private set

    private val _eventCreated = MutableSharedFlow<Boolean>()
    val eventCreated = _eventCreated.asSharedFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    val isFormValid: Boolean
        get() = title.isNotBlank() && description.isNotBlank() && !isLoading

    fun createEvent(imageBytes: ByteArray?) {
        viewModelScope.launch {
            isLoading = true
            try {
                val user = supabase.auth.currentSessionOrNull()?.user
                if (user == null) {
                    _error.emit("Vous devez être connecté pour créer un événement")
                    return@launch
                }

                val eventUniqueId = UUID.randomUUID().toString()

                var imageUrl: String? = null
                if (imageBytes != null) {
                    val fileName = "event_${eventUniqueId}_${System.currentTimeMillis()}"
                    imageUrl = databaseService.uploadImage(fileName, imageBytes)
                }

                val newEvent = GeoEvent(
                    id = eventUniqueId,
                    title = title,
                    description = description,
                    latitude = 49.8887,
                    longitude = 2.2858,
                    image_url = imageUrl,
                    user_id = user.id
                )

                databaseService.addEvent(newEvent)
                _eventCreated.emit(true)
            } catch (e: Exception) {
                _error.emit("Erreur : ${e.message ?: "Une erreur est survenue"}")
            } finally {
                isLoading = false
            }
        }
    }
}
