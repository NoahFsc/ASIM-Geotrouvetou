package fr.miage.geotrouvetou.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geotrouvetou.R

private val CheckboxSize = 44.dp
private val CheckboxCorner = RoundedCornerShape(12.dp)
private val RingCorner = RoundedCornerShape(16.dp)

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val primary = colorResource(R.color.primary_600)
    val primary500 = colorResource(R.color.primary_500)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // L'enveloppe externe absorbe le halo de focus sans décaler le contenu intérieur
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(CheckboxSize + 8.dp)
        ) {
            if (isPressed) {
                Box(
                    modifier = Modifier
                        .size(CheckboxSize + 8.dp)
                        .border(1.5.dp, primary, RingCorner)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(CheckboxSize)
                    .clip(CheckboxCorner)
                    .background(if (checked) primary else Color.White)
                    .border(1.5.dp, primary500, CheckboxCorner)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) { onCheckedChange(!checked) }
            ) {
                if (checked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.text_darker),
        )
    }
}

@Composable
private fun PreviewWrapper(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .background(colorResource(R.color.background))
            .padding(16.dp)
    ) { content() }
}

@Preview(name = "Checkbox – Unchecked")
@Composable
private fun CheckboxUncheckedPreview() {
    PreviewWrapper {
        Checkbox(checked = false, onCheckedChange = {}, label = "Texte")
    }
}

@Preview(name = "Checkbox – Checked")
@Composable
private fun CheckboxCheckedPreview() {
    PreviewWrapper {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Checkbox(checked = false, onCheckedChange = {}, label = "Texte")
            Checkbox(checked = true, onCheckedChange = {}, label = "Texte")
        }
    }
}
