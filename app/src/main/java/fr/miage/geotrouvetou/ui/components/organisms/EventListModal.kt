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
fun EventListModal(
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
            EventListContent(onEventClick = { selectedEvent = it })
        } else {
            EventDetailContent(
                onBackClick = { selectedEvent = null },
                event = selectedEvent!!
            )
        }
    }
}

@Composable
fun EventListContent(
    onEventClick: (EventProposal) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }

    val proposals = listOf(
        EventProposal(
            tag = TagStatus.SOON,
            title = "Citadelle et Rives du Doubs",
            duration = "02:10h",
            distance = "7,20km",
            isRecommended = true,
            attendance = "Faible",
            date = "Samedi, 28 Avril",
            time = "09:00",
            locationName = "Citadelle de Besançon",
            locationDetail = "Besançon, France",
            description = "Découvrez la citadelle de Besançon et les rives du Doubs lors de cette randonnée urbaine et historique."
        ),
        EventProposal(
            tag = TagStatus.NEW,
            title = "Grande Forêt de Chailluz",
            duration = "01:45h",
            distance = "6,50km",
            isRecommended = false,
            attendance = "Faible",
            date = "Dimanche, 29 Avril",
            time = "10:00",
            locationName = "Forêt de Chailluz",
            locationDetail = "Besançon, France",
            description = "Ce parcours accessible aux chiens vous fait découvrir la grande forêt de Chailluz dans les environs de Besançon. Vous traverserez environ 6 kilomètres de nature et il est possible de voir des chevreuils en liberté selon la saison."
        ),
        EventProposal(
            tag = TagStatus.DONE,
            title = "Fort de Chaudanne",
            duration = "03:30h",
            distance = "11,8km",
            isRecommended = false,
            attendance = "Faible",
            date = "Lundi, 30 Avril",
            time = "08:30",
            locationName = "Fort de Chaudanne",
            locationDetail = "Besançon, France",
            description = "Une randonnée plus sportive pour atteindre le Fort de Chaudanne et profiter d'une vue imprenable sur Besançon."
        ),
        EventProposal(
            tag = TagStatus.SOON,
            title = "Chapelle des Buis",
            duration = "02:00h",
            distance = "5,40km",
            isRecommended = false,
            attendance = "Faible",
            date = "Mardi, 1 Mai",
            time = "14:00",
            locationName = "Chapelle des Buis",
            locationDetail = "Besançon, France",
            description = "Petite randonnée tranquille vers la Chapelle des Buis, parfaite pour une sortie en famille."
        )
    )

    val filteredProposals = remember(searchQuery) {
        proposals.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SearchBar(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = "Choisir votre destination"
        )

        Text(
            text = "Propositions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_darker)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredProposals) { proposal ->
                EventCard(
                    tag = proposal.tag,
                    title = proposal.title,
                    date = proposal.duration,
                    time = proposal.distance,
                    isRecommended = proposal.isRecommended,
                    attendance = proposal.attendance,
                    onClick = { onEventClick(proposal) }
                )
            }
        }
    }
}

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
        EventListContent(onEventClick = {})
    }
}
