package fr.miage.geoevent.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class BadgeStatus {
    VALIDATED,
    REFUSED,
    WAITING
}

@Composable
fun StatusBadge(
    status: BadgeStatus,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        BadgeStatus.VALIDATED -> Color(0xFF56A892)
        BadgeStatus.REFUSED -> Color(0xFFFF5F57)
        BadgeStatus.WAITING -> Color(0xFFAAB2C0)
    }

    Box(
        modifier = modifier
            .size(86.dp)
            .background(
                color = backgroundColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        when (status) {
            BadgeStatus.VALIDATED -> {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Validé",
                    tint = Color.White,
                    modifier = Modifier.size(72.dp)
                )
            }

            BadgeStatus.REFUSED -> {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Refusé",
                    tint = Color.White,
                    modifier = Modifier.size(72.dp)
                )
            }

            BadgeStatus.WAITING -> {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1E1E)
@Composable
private fun StatusBadgePreview() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatusBadge(status = BadgeStatus.VALIDATED)

        Spacer(modifier = Modifier.height(140.dp))

        StatusBadge(status = BadgeStatus.REFUSED)

        Spacer(modifier = Modifier.height(140.dp))

        StatusBadge(status = BadgeStatus.WAITING)
    }
}