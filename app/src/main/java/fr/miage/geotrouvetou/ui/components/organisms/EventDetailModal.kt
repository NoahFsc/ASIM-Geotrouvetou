package fr.miage.geotrouvetou.ui.components.organisms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geotrouvetou.R
import fr.miage.geotrouvetou.ui.components.atoms.Button
import fr.miage.geotrouvetou.ui.components.atoms.TagStatus
import fr.miage.geotrouvetou.ui.components.molecules.EventDetailCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailModal(
    onDismissRequest: () -> Unit,
    onBackClick: () -> Unit,
    event: EventProposal,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    Modal(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        EventDetailContent(onBackClick = onBackClick, event = event)
    }
}

@Composable
fun EventDetailContent(
    onBackClick: () -> Unit,
    event: EventProposal,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable(onClick = onBackClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = colorResource(R.color.text_darker),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Revenir aux propositions",
                    fontSize = 16.sp,
                    color = colorResource(R.color.text_darker)
                )
            }

            Button(
                text = "Rejoindre",
                onClick = {},
                leftIcon = Icons.Default.Add,
                fullWidth = false,
                modifier = Modifier.height(40.dp)
            )
        }

        EventDetailCard(
            date = event.date,
            time = event.time,
            locationName = event.locationName,
            locationDetail = event.locationDetail
        )

        // Image placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colorResource(R.color.text_disabled))
        ) {
            // Note: In a real app, use AsyncImage (Coil/Glide)
            Text(
                text = "Image pour ${event.title}",
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(R.color.text_lighter)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "A propos de l’événement",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_darker)
            )
            Text(
                text = event.description,
                fontSize = 16.sp,
                color = colorResource(R.color.text_light),
                lineHeight = 24.sp
            )
        }

        Text(
            text = "Participants (12)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_darker),
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EventDetailContentPreview() {
    Box(modifier = Modifier.background(colorResource(R.color.background))) {
        EventDetailContent(
            onBackClick = {},
            event = EventProposal(
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
                description = "Ce parcours accessible aux chiens vous fait découvrir la grande forêt de Chailluz dans les environs de Besançon."
            )
        )
    }
}
