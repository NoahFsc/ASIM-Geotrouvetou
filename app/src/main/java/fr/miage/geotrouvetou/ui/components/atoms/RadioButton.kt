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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geotrouvetou.R

private val RadioSize = 44.dp
private val InnerDotSize = 28.dp

@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val primary = colorResource(R.color.primary_500)
    val primary600 = colorResource(R.color.primary_600)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(RadioSize + 8.dp)
        ) {
            if (isPressed) {
                Box(
                    modifier = Modifier
                        .size(RadioSize + 8.dp)
                        .border(1.5.dp, primary600, CircleShape)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(RadioSize)
                    .border(1.5.dp, primary, CircleShape)
                    .background(Color.White, CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) { onClick() }
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(InnerDotSize)
                            .background(primary600, CircleShape)
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

@Preview(name = "RadioButton – Unselected")
@Composable
private fun RadioButtonUnselectedPreview() {
    PreviewWrapper {
        RadioButton(selected = false, onClick = {}, label = "Texte")
    }
}

@Preview(name = "RadioButton – All States")
@Composable
private fun RadioButtonAllStatesPreview() {
    PreviewWrapper {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            RadioButton(selected = false, onClick = {}, label = "Texte")
            RadioButton(selected = true, onClick = {}, label = "Texte")
        }
    }
}
