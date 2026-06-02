package fr.miage.geotrouvetou.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geotrouvetou.R
import fr.miage.geotrouvetou.data.geocoding.NominatimPlace
import fr.miage.geotrouvetou.data.geocoding.NominatimService
import fr.miage.geotrouvetou.domain.models.Evenement
import fr.miage.geotrouvetou.ui.components.atoms.TagStatus
import fr.miage.geotrouvetou.ui.components.molecules.EventCard
import fr.miage.geotrouvetou.ui.utils.formattedDate
import fr.miage.geotrouvetou.ui.utils.formattedTime
import fr.miage.geotrouvetou.ui.utils.tagStatus
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListModal(
    events: List<Evenement>,
    onDismissRequest: () -> Unit,
    title: String = "Propositions",
    onPlaceSelected: ((Double, Double) -> Unit)? = null,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var selectedEvent by remember { mutableStateOf<Evenement?>(null) }

    Modal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        if (selectedEvent == null) {
            EventListContent(
                events = events,
                title = title,
                onEventClick = { selectedEvent = it },
                onPlaceSelected = onPlaceSelected
            )
        } else {
            EvenementDetailContent(
                event = selectedEvent!!,
                onBackClick = { selectedEvent = null }
            )
        }
    }
}

@Composable
fun EventListContent(
    events: List<Evenement>,
    onEventClick: (Evenement) -> Unit,
    title: String = "Propositions",
    onPlaceSelected: ((Double, Double) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    var placeResults by remember { mutableStateOf<List<NominatimPlace>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    // Recherche de lieu via Nominatim avec debounce (seulement si onPlaceSelected est fourni)
    if (onPlaceSelected != null) {
        LaunchedEffect(searchQuery) {
            if (searchQuery.isBlank()) {
                placeResults = emptyList()
                isSearching = false
                return@LaunchedEffect
            }
            delay(500)
            isSearching = true
            try {
                placeResults = NominatimService.search(searchQuery)
            } catch (e: Exception) {
                placeResults = emptyList()
            } finally {
                isSearching = false
            }
        }
    }

    val isPlaceSearch = onPlaceSelected != null && searchQuery.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SearchBar(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Rechercher un lieu"
        )

        if (!isPlaceSearch) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_darker)
            )

            if (events.isEmpty()) {
                Text(
                    text = "Aucun événement dans cette zone",
                    fontSize = 14.sp,
                    color = colorResource(R.color.text_light)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(events) { event ->
                    EventCard(
                        tag = event.tagStatus(),
                        title = event.title,
                        date = event.formattedDate(),
                        time = event.formattedTime(),
                        isRecommended = false,
                        attendance = null,
                        onClick = { onEventClick(event) }
                    )
                }
            }
        } else {
            Text(
                text = "Lieux",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_darker)
            )

            if (isSearching) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = colorResource(R.color.primary_400)
                    )
                }
            } else if (placeResults.isEmpty()) {
                Text(
                    text = "Aucun lieu trouvé",
                    fontSize = 14.sp,
                    color = colorResource(R.color.text_light)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(placeResults) { place ->
                        PlaceResultCard(
                            place = place,
                            onClick = { onPlaceSelected(place.latitude, place.longitude) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceResultCard(
    place: NominatimPlace,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.white))
            .border(1.dp, colorResource(R.color.text_disabled), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = colorResource(R.color.primary_400),
                modifier = Modifier.size(20.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = place.mainLine,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.text_darker),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (place.countryLine.isNotBlank()) {
                    Text(
                        text = place.countryLine,
                        fontSize = 12.sp,
                        color = colorResource(R.color.text_light),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colorResource(R.color.text_light),
            modifier = Modifier.size(20.dp)
        )
    }
}

// ---- Kept for EventHistoryModal compatibility ----

data class EventProposal(
    val tag: TagStatus,
    val title: String,
    val duration: String,
    val distance: String,
    val isRecommended: Boolean,
    val attendance: String,
    val date: String,
    val time: String,
    val locationName: String,
    val locationDetail: String,
    val description: String
)

@Preview(showBackground = true)
@Composable
private fun EventListContentPreview() {
    Box(modifier = Modifier.background(colorResource(R.color.background))) {
        EventListContent(events = emptyList(), onEventClick = {})
    }
}
