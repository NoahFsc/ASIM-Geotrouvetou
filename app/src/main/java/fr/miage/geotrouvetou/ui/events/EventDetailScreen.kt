package fr.miage.geotrouvetou.ui.events

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.miage.geotrouvetou.App
import fr.miage.geotrouvetou.BuildConfig
import fr.miage.geotrouvetou.R
import fr.miage.geotrouvetou.data.backend.SupabaseDatabaseService
import fr.miage.geotrouvetou.domain.models.Evenement
import fr.miage.geotrouvetou.ui.components.atoms.Button
import fr.miage.geotrouvetou.ui.components.atoms.ButtonVariant
import fr.miage.geotrouvetou.ui.components.molecules.EventDetailCard
import java.text.SimpleDateFormat
import java.util.Locale

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
                participantsCount = viewModel.participantsCount,
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
    participantsCount: Int,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // Formatage de la date ISO vers lisible (take 19 pour supporter divers formats ISO)
    val (displayDate, displayTime) = try {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = isoFormat.parse(event.event_date?.take(19) ?: "")
        val dateFormatter = SimpleDateFormat("EEEE, dd MMMM", Locale.FRANCE)
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.FRANCE)
        Pair(dateFormatter.format(date!!).replaceFirstChar { it.uppercase() }, timeFormatter.format(date))
    } catch (e: Exception) {
        Pair("Date inconnue", "--:--")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Header / Retour
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
            // Titre
            Text(
                text = event.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.primary_600),
                lineHeight = 34.sp
            )

            // Image
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(event.image_url)
                    .addHeader("apikey", BuildConfig.SUPABASE_KEY)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorResource(R.color.text_disabled)),
                onError = {
                    Log.e("EventDetail", "Erreur chargement image: ${it.result.throwable.message}")
                    Log.d("EventDetail", "URL tentée: ${event.image_url}")
                }
            )

            // Boutons d'action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    text = if (isJoined) "Déjà inscrit" else "Enregistrer",
                    onClick = { if (!isJoined) onJoinClick() },
                    enabled = !isJoined,
                    variant = ButtonVariant.Fill,
                    leftIcon = Icons.Default.Add,
                    modifier = Modifier.weight(1f)
                )
            }

            // Carte d'infos
            EventDetailCard(
                date = displayDate,
                time = displayTime,
                locationName = "Coordonnées",
                locationDetail = "Lat: ${event.latitude}, Lon: ${event.longitude}"
            )

            // A propos
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "A propos de l'événement",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.text_darker)
                )
                Text(
                    text = event.description,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = colorResource(R.color.text_lighter)
                )
            }

            // Participants
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Participants ($participantsCount)",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.text_darker)
                )
                
                ParticipantStack(count = participantsCount)
            }
        }
    }
}

@Composable
fun ParticipantStack(count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val displayCount = 4
        for (i in 0 until displayCount.coerceAtMost(count)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .offset(x = if (i > 0) ((-12) * i).dp else 0.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .clip(CircleShape)
                    .background(colorResource(R.color.text_disabled)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        if (count > displayCount) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .offset(x = ((-12) * displayCount).dp)
                    .border(2.dp, Color.White, CircleShape)
                    .clip(CircleShape)
                    .background(colorResource(R.color.text_disabled)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ ${count - displayCount}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.text_dark)
                )
            }
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
            event_date = "2024-04-28T09:00:00Z"
        ),
        onBackClick = {},
        isJoined = false,
        participantsCount = 1,
        onJoinClick = {}
    )
}
