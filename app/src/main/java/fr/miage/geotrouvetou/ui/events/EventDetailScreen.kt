package fr.miage.geotrouvetou.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.miage.geotrouvetou.App
import fr.miage.geotrouvetou.R
import fr.miage.geotrouvetou.data.backend.SupabaseDatabaseService
import fr.miage.geotrouvetou.domain.models.Evenement
import fr.miage.geotrouvetou.ui.components.atoms.Button
import fr.miage.geotrouvetou.ui.components.atoms.ButtonVariant
import fr.miage.geotrouvetou.ui.components.organisms.EventDetailBody
import fr.miage.geotrouvetou.ui.utils.formattedDateLong
import fr.miage.geotrouvetou.ui.utils.formattedTime

@Composable
fun EventDetailScreen(
    eventId: String?,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: EventDetailViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = context.applicationContext as App
                val databaseService = SupabaseDatabaseService(app.supabase)
                @Suppress("UNCHECKED_CAST")
                return EventDetailViewModel(databaseService, app.supabase) as T
            }
        }
    )

    LaunchedEffect(eventId) {
        eventId?.let { viewModel.loadEvent(it) }
    }

    if (viewModel.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colorResource(R.color.primary_500))
        }
    } else {
        viewModel.event?.let { event ->
            EventDetailContent(
                event = event,
                onBackClick = onBackClick,
                isJoined = viewModel.isJoined,
                onJoinClick = { viewModel.joinEvent() }
            )
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Événement introuvable")
            }
        }
    }
}

@Composable
fun EventDetailContent(
    event: Evenement,
    onBackClick: () -> Unit,
    isJoined: Boolean,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageUrl = event.image_url
        ?: "https://picsum.photos/seed/${event.title.hashCode()}/800/400"
    val date = event.formattedDateLong()
    val time = event.formattedTime()
    val coordsLabel = "Lat: ${event.latitude}, Lon: ${event.longitude}"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clickable { onBackClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = colorResource(R.color.text_light),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Retour",
                fontSize = 18.sp,
                color = colorResource(R.color.text_light)
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = event.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.primary_600),
                lineHeight = 34.sp
            )

            Button(
                text = if (isJoined) "Déjà inscrit" else "Enregistrer",
                onClick = { if (!isJoined) onJoinClick() },
                enabled = !isJoined,
                variant = ButtonVariant.Fill,
                leftIcon = Icons.Default.Add,
                modifier = Modifier.fillMaxWidth()
            )

            EventDetailBody(
                imageUrl = imageUrl,
                date = date,
                time = time,
                locationName = "Coordonnées",
                locationDetail = coordsLabel,
                description = event.description,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventDetailScreenPreview() {
    EventDetailContent(
        event = Evenement(
            title = "Grande Forêt de Chailluz",
            description = "Ce parcours accessible aux chiens vous fait découvrir la grande forêt de Chailluz...",
            latitude = 0.0,
            longitude = 0.0,
            event_date = "2024-04-28T09:00:00"
        ),
        onBackClick = {},
        isJoined = false,
        onJoinClick = {}
    )
}
