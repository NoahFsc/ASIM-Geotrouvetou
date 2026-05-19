package fr.miage.geoevent.ui.events

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.miage.geoevent.GeoEventApplication
import fr.miage.geoevent.data.backend.SupabaseDatabaseService
import fr.miage.geoevent.ui.components.AppButton
import fr.miage.geoevent.ui.components.AppImagePicker
import fr.miage.geoevent.ui.components.AppTextField
import fr.miage.geoevent.ui.components.AppTopBar

class CreateEventActivity : AppCompatActivity() {

    private val viewModel: CreateEventViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val supabase = (applicationContext as GeoEventApplication).supabase
                val databaseService = SupabaseDatabaseService(supabase)
                return CreateEventViewModel(databaseService, supabase) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Observation du succès pour fermer l'activité
            LaunchedEffect(Unit) {
                viewModel.eventCreated.collect {
                    Toast.makeText(this@CreateEventActivity, "Événement créé avec succès !", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            // Observation des erreurs
            LaunchedEffect(Unit) {
                viewModel.error.collect {
                    Toast.makeText(this@CreateEventActivity, it, Toast.LENGTH_LONG).show()
                }
            }

            CreateEventContent(
                viewModel = viewModel,
                onBackClick = { finish() },
                onCreateEventClick = {
                    val bytes = viewModel.imageUri?.let { uri ->
                        contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    }
                    viewModel.createEvent(bytes)
                }
            )
        }
    }
}

@Composable
fun CreateEventContent(
    viewModel: CreateEventViewModel,
    onBackClick: () -> Unit,
    onCreateEventClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Créer un événement",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = "Titre",
                placeholder = "Ex: Concert au parc",
                required = true,
                enabled = !viewModel.isLoading
            )

            AppTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = "Description",
                placeholder = "Décrivez l'événement",
                enabled = !viewModel.isLoading
            )

            Text(
                text = "Position : 20 avenue de la sagesse, Amiens",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            AppImagePicker(
                selectedUri = viewModel.imageUri,
                onUriSelected = { viewModel.imageUri = it }
            )

            AppButton(
                text = if (viewModel.isLoading) "Création..." else "Créer l'événement",
                onClick = onCreateEventClick,
                enabled = viewModel.isFormValid,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
