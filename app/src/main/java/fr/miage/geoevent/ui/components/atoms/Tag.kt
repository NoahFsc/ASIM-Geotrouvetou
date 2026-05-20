package fr.miage.geoevent.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class TagStatus(
    val label: String,
    val backgroundColor: Color
) {
    NEW(
        label = "NOUVEAU",
        backgroundColor = Color(0xFF10B981)
    ),
    SOON(
        label = "BIENTÔT",
        backgroundColor = Color(0xFFFFA552)
    ),
    DONE(
        label = "TERMINÉ",
        backgroundColor = Color(0xFFFF5F57)
    )
}

@Composable
fun StatusTag(
    status: TagStatus,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = status.backgroundColor,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(
                horizontal = 28.dp,
                vertical = 14.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.label,
            color = Color(0xFF0F172A),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1E1E)
@Composable
private fun StatusTagPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        StatusTag(status = TagStatus.NEW)

        Box(modifier = Modifier.padding(top = 56.dp)) {
            StatusTag(status = TagStatus.SOON)
        }

        Box(modifier = Modifier.padding(top = 56.dp)) {
            StatusTag(status = TagStatus.DONE)
        }
    }
}