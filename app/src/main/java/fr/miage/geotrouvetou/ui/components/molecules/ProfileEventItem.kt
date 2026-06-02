package fr.miage.geotrouvetou.ui.components.molecules

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.miage.geotrouvetou.BuildConfig
import fr.miage.geotrouvetou.R
import fr.miage.geotrouvetou.domain.models.Evenement
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProfileEventItem(event: Evenement, onClick: () -> Unit) {
    val context = LocalContext.current
    val (displayDate, displayTime) = try {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = isoFormat.parse(event.event_date?.take(19) ?: "")
        val dateFormatter = SimpleDateFormat("dd/MM", Locale.FRANCE)
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.FRANCE)
        Pair(dateFormatter.format(date!!), timeFormatter.format(date))
    } catch (e: Exception) {
        Pair("--/--", "--:--")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(event.image_url)
                .addHeader("apikey", BuildConfig.SUPABASE_KEY)
                .crossfade(true)
                .build(),
            contentDescription = event.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorResource(R.color.text_disabled)),
            onError = {
                Log.e("ProfileEventItem", "Erreur chargement image: ${it.result.throwable.message}")
            }
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = event.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_darker),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = colorResource(R.color.text_lighter),
                        modifier = Modifier.size(14.dp),
                    )
                    Text(displayDate, fontSize = 13.sp, color = colorResource(R.color.text_lighter))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = colorResource(R.color.text_lighter),
                        modifier = Modifier.size(14.dp),
                    )
                    Text(displayTime, fontSize = 13.sp, color = colorResource(R.color.text_lighter))
                }
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = colorResource(R.color.text_darker),
            modifier = Modifier.size(20.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileEventItemPreview() {
    ProfileEventItem(
        event = Evenement(
            title = "Randonnée en forêt",
            description = "Une petite marche",
            latitude = 0.0,
            longitude = 0.0,
            event_date = "2024-04-28T09:00:00Z"
        ),
        onClick = {}
    )
}
