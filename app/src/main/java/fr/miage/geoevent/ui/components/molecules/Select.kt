package fr.miage.geoevent.ui.components.molecules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geoevent.R

private val SelectShape = RoundedCornerShape(12.dp)

@Composable
fun Select(
    value: String?,
    onValueChange: (String) -> Unit,
    options: List<String>,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    required: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor = colorResource(R.color.text_lighter)
    val textColor = colorResource(R.color.text_darker)

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = buildAnnotatedString {
                    append(label)
                    if (required) {
                        withStyle(SpanStyle(color = colorResource(R.color.danger_500))) {
                            append(" *")
                        }
                    }
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SelectShape)
                .background(Color.White)
                .border(1.dp, borderColor, SelectShape)
                .clickable { expanded = !expanded }
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value ?: placeholder,
                color = if (value != null) textColor else colorResource(R.color.text_placeholder),
                fontSize = 16.sp,
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = colorResource(R.color.text_lighter),
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(SelectShape)
                        .background(Color.White)
                        .border(1.dp, borderColor, SelectShape)
                ) {
                    options.forEach { option ->
                        Text(
                            text = option,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValueChange(option)
                                    expanded = false
                                }
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            color = textColor,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
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

@Preview(name = "Select – Closed")
@Composable
private fun SelectClosedPreview() {
    PreviewWrapper {
        Select(
            value = null,
            onValueChange = {},
            options = listOf("Demande utilisateur", "Inactivité", "Autre"),
            placeholder = "Motif de la suppression",
        )
    }
}

@Preview(name = "Select – With Label")
@Composable
private fun SelectWithLabelPreview() {
    PreviewWrapper {
        Select(
            value = null,
            onValueChange = {},
            options = listOf("Demande utilisateur", "Inactivité", "Autre"),
            placeholder = "Motif de la suppression",
            label = "Motif",
            required = true,
        )
    }
}

@Preview(name = "Select – With Value")
@Composable
private fun SelectWithValuePreview() {
    PreviewWrapper {
        Select(
            value = "Inactivité",
            onValueChange = {},
            options = listOf("Demande utilisateur", "Inactivité", "Autre"),
            placeholder = "Motif de la suppression",
        )
    }
}

@Preview(name = "Select – Text No Required")
@Composable
private fun SelectTextNoRequiredPreview() {
    PreviewWrapper {
        Select(
            value = null,
            onValueChange = {},
            options = listOf("Demande utilisateur", "Inactivité", "Autre"),
            placeholder = "Motif de la suppression",
            label = "Motif",
            required = false,
        )
    }
}