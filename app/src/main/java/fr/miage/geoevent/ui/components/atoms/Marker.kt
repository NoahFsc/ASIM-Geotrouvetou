package fr.miage.geoevent.ui.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import fr.miage.geoevent.R

@Composable
fun MarkerIcon(
    size: Dp = 48.dp,
    borderWidth: Dp = 3.dp
) {
    MaterialTheme {
        val bgColor = colorResource(id = R.color.primary_400)
        val flagColor = colorResource(id = R.color.primary_600)

        Box(
            modifier = Modifier
                .size(size)
                .background(color = bgColor, shape = CircleShape)
                .border(width = borderWidth, color = Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Flag,
                contentDescription = null,
                tint = flagColor,
                modifier = Modifier.size(size * 0.65f)
            )
        }
    }
}

