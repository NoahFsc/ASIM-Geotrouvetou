package fr.miage.geoevent.ui.events

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.miage.geoevent.GeoEventApplication
import fr.miage.geoevent.R
import fr.miage.geoevent.data.backend.SupabaseDatabaseService
import fr.miage.geoevent.ui.components.atoms.Button
import fr.miage.geoevent.ui.components.atoms.ImageUploader
import fr.miage.geoevent.ui.components.atoms.Input
import fr.miage.geoevent.ui.components.atoms.TextArea

@Composable
fun CreateEventScreen(
    onBackClick: () -> Unit,
    onEventCreated: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: CreateEventViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = context.applicationContext as GeoEventApplication
                val databaseService = SupabaseDatabaseService(app.supabase)
                @Suppress("UNCHECKED_CAST")
                return CreateEventViewModel(databaseService, app.supabase) as T
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.eventCreated.collect {
            Toast.makeText(context, "Événement créé avec succès !", Toast.LENGTH_SHORT).show()
            onEventCreated()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.error.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = colorResource(R.color.text_darker),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Créer un événement",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_darker),
            )
        }

        Input(
            value = viewModel.title,
            onValueChange = { viewModel.title = it },
            placeholder = "Ex: Concert au parc",
            label = "Titre",
            required = true,
        )

        TextArea(
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            placeholder = "Décrivez l'événement",
            label = "Description",
            maxLength = 500,
        )

        Text(
            text = "Position : 20 avenue de la sagesse, Amiens",
            fontSize = 14.sp,
            color = colorResource(R.color.primary_500),
        )

        ImageUploader(
            imageUri = viewModel.imageUri,
            onImageSelected = { viewModel.imageUri = it },
            label = "Photo",
        )

        Button(
            text = if (viewModel.isLoading) "Création..." else "Créer l'événement",
            onClick = {
                val bytes = viewModel.imageUri?.let { uri ->
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }
                viewModel.createEvent(bytes)
            },
            enabled = viewModel.isFormValid,
        )
    }
}
