package fr.miage.geotrouvetou.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geotrouvetou.R
import fr.miage.geotrouvetou.ui.components.atoms.TagStatus
import fr.miage.geotrouvetou.ui.components.molecules.EventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventHistoryModal(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var selectedEvent by remember { mutableStateOf<EventProposal?>(null) }

    Modal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        if (selectedEvent == null) {
            EventHistoryContent(onEventClick = { selectedEvent = it })
        } else {
            EventDetailContent(
                onBackClick = { selectedEvent = null },
                event = selectedEvent!!
            )
        }
    }
}

@Composable
fun EventHistoryContent(
    onEventClick: (EventProposal) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }

    val allPastEvents = listOf(
        EventProposal(
            tag = TagStatus.DONE,
            title = "Forêt d'Ornans",
            duration = "02:30h",
            distance = "8,40km",
            isRecommended = false,
            attendance = "Faible",
            date = "28/04",
            time = "09:00",
            locationName = "Forêt d'Ornans",
            locationDetail = "Ornans, France",
            description = "Une magnifique randonnée dans les bois entourant la ville d'Ornans, offrant des vues imprenable sur la vallée de la Loue."
        ),
        EventProposal(
            tag = TagStatus.DONE,
            title = "Source de la Loue",
            duration = "01:15h",
            distance = "4,20km",
            isRecommended = true,
            attendance = "Moyenne",
            date = "10/04",
            time = "08:30",
            locationName = "Ouhans",
            locationDetail = "Ouhans, France",
            description = "Le sentier mène directement à l'impressionnante résurgence de la Loue sortant d'une immense paroi rocheuse."
        ),
        EventProposal(
            tag = TagStatus.SOON,
            title = "Citadelle de Besançon",
            duration = "02:00h",
            distance = "5,00km",
            isRecommended = false,
            attendance = "Élevée",
            date = "15/04",
            time = "14:00",
            locationName = "Besançon",
            locationDetail = "Besançon, France",
            description = "Tour de la Citadelle Vauban, patrimoine mondial de l'UNESCO, avec une vue panoramique sur la boucle du Doubs."
        ),
        EventProposal(
            tag = TagStatus.NEW,
            title = "Lac de Vouglans",
            duration = "04:30h",
            distance = "15,00km",
            isRecommended = false,
            attendance = "Faible",
            date = "05/05",
            time = "10:30",
            locationName = "Pont de Poitte",
            locationDetail = "Jura, France",
            description = "Longue randonnée le long des rives turquoises du lac de Vouglans, le troisième plus grand lac artificiel de France."
        )
    )

    val filteredEvents = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            allPastEvents
        } else {
            allPastEvents.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }
    }

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
            placeholder = "Choisir votre destination"
        )

        Text(
            text = "Historique",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_darker)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredEvents) { event ->
                EventCard(
                    tag = event.tag,
                    title = event.title,
                    date = event.date,
                    time = event.time,
                    onClick = { onEventClick(event) },
                    onDelete = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EventHistoryContentPreview() {
    Box(modifier = Modifier.background(colorResource(R.color.background))) {
        EventHistoryContent(onEventClick = {})
    }
}
