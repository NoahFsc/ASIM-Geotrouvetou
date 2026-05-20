package fr.miage.geotrouvetou.ui.components.molecules

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geotrouvetou.R
import fr.miage.geotrouvetou.ui.components.atoms.Button

@Composable
fun ProfileCard(
    name: String,
    username: String,
    onInvite: () -> Unit,
    modifier: Modifier = Modifier,
    avatar: Painter? = null,
) {
    var invited by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.white))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Avatar(painter = avatar)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_darker),
            )
            Text(
                text = "@$username",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = colorResource(R.color.text_lighter),
            )
        }

        Button(
            text = if (invited) "Invité" else "Inviter",
            onClick = { invited = true; onInvite() },
            leftIcon = if (invited) Icons.Filled.Check else Icons.Filled.Add,
            enabled = !invited,
            fullWidth = false,
        )
    }
}

@Composable
private fun Avatar(painter: Painter?) {
    val size = Modifier
        .size(52.dp)
        .clip(CircleShape)

    if (painter != null) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = size,
        )
    } else {
        Box(
            modifier = size.background(colorResource(R.color.text_disabled)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = colorResource(R.color.text_light),
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Preview(name = "ProfileCard – sans avatar")
@Composable
private fun ProfileCardPreview() {
    Box(modifier = Modifier.background(colorResource(R.color.text_darker)).padding(16.dp)) {
        ProfileCard(name = "Alex Dubois", username = "alex_dubois", onInvite = {})
    }
}
