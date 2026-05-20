package fr.miage.geoevent.ui.components.atomes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geoevent.R

@Composable
fun Input(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    label: String? = null,
    required: Boolean = false,
    erreur: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = buildAnnotatedString {
                    append(label)
                    if (required) {
                        withStyle(SpanStyle(color = colorResource(id = R.color.danger_500))) {
                            append(" *")
                        }
                    }
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.text_darker),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 16.sp),
            placeholder = {
                Text(text = placeholder, color = colorResource(id = R.color.text_placeholder), fontSize = 16.sp)
            },
            leadingIcon = leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = colorResource(id = R.color.text_lighter)) }
            },
            trailingIcon = trailingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = colorResource(id = R.color.text_lighter)) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    colorResource(id = if (erreur != null) R.color.danger_500 else R.color.text_lighter),
                    RoundedCornerShape(8.dp)
                ),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = colorResource(id = R.color.text_darker),
                unfocusedTextColor = colorResource(id = R.color.text_darker),
            ),
            singleLine = true,
        )
        if (erreur != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = erreur,
                fontSize = 16.sp,
                color = colorResource(id = R.color.danger_500),
            )
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

@Preview(name = "Fill – Default")
@Composable
private fun FillDefaultPreview() {
    PreviewWrapper {
        Input(
            value = "",
            onValueChange = {},
            placeholder = "Rechercher un événement",
        )
    }
}

@Preview(name = "Fill – With Text")
@Composable
private fun FillWithTextEmptyPreview() {
    PreviewWrapper {
        Input(
            value = "Fête de la musique",
            onValueChange = {},
            placeholder = "Rechercher un événement",
        )
    }
}

@Preview(name = "Fill – With Icons")
@Composable
private fun FillWithIconsPreview() {
    PreviewWrapper {
        Input(
            value = "",
            onValueChange = {},
            placeholder = "Rechercher un événement",
            leadingIcon = Icons.Default.Search,
            trailingIcon = Icons.Default.Clear
        )
    }
}

@Preview(name = "Fill – With Icon Left")
@Composable
private fun FillWithIconLeftPreview() {
    PreviewWrapper {
        Input(
            value = "",
            onValueChange = {},
            placeholder = "Rechercher un événement",
            leadingIcon = Icons.Default.Search,
        )
    }
}

@Preview(name = "Fill – With Icon Right")
@Composable
private fun FillWithIconRightPreview() {
    PreviewWrapper {
        Input(
            value = "",
            onValueChange = {},
            placeholder = "Rechercher un événement",
            trailingIcon = Icons.Default.Clear
        )
    }
}

@Preview(name = "Fill – With Text")
@Composable
private fun FillWithTextPreview() {
    PreviewWrapper {
        Input(
            value = "Fête de la musique",
            onValueChange = {},
            placeholder = "Rechercher un événement",
            leadingIcon = Icons.Default.Search,
            trailingIcon = Icons.Default.Clear
        )
    }
}

@Preview(name = "Fill – With Label")
@Composable
private fun FillWithLabelPreview() {
    PreviewWrapper {
        Input(
            value = "Fête de la musique",
            onValueChange = {},
            placeholder = "Rechercher un événement",
            label = "Titre de l'événement"
        )
    }
}

@Preview(name = "Fill – With Label Required")
@Composable
private fun FillWithLabelRequiredPreview() {
    PreviewWrapper {
        Input(
            value = "Fête de la musique",
            onValueChange = {},
            placeholder = "Rechercher un événement",
            label = "Titre de l'événement",
            required = true
        )
    }
}

@Preview(name = "Fill – With Error")
@Composable
private fun FillWithErrorPreview() {
    PreviewWrapper {
        Input(
            value = "Fête de la musique",
            onValueChange = {},
            placeholder = "Rechercher un événement",
            label = "Titre de l'événement",
            required = true,
            erreur = "Ce champ est requis"
        )
    }
}
