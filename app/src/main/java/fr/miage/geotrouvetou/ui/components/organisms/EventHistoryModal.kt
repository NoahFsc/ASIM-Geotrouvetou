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
    Modal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        EventHistoryContent()
    }
}

@Composable
fun EventHistoryContent(
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }

    val allPastEvents = listOf(
        EventData(TagStatus.NEW, "Forêt d'Ornans", "28/04", "09:00"),
        EventData(TagStatus.NEW, "Source de la Loue", "10/04", "08:30"),
        EventData(TagStatus.NEW, "Citadelle de Besançon", "15/04", "14:00"),
        EventData(TagStatus.NEW, "Lac de Vouglans", "05/05", "10:30")
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
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
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
                    onClick = {},
                    onDelete = {}
                )
            }
        }
    }
}

private data class EventData(
    val tag: TagStatus,
    val title: String,
    val date: String,
    val time: String
)

@Preview(showBackground = true)
@Composable
private fun EventHistoryContentPreview() {
    Box(modifier = Modifier.background(colorResource(R.color.background))) {
        EventHistoryContent()
    }
}
