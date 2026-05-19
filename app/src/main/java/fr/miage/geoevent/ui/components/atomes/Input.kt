package fr.miage.geoevent.ui.components.atomes

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import fr.miage.geoevent.R

@Composable
fun Input(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = placeholder, color = colorResource(id = R.color.text_placeholder))
        },
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null, tint = colorResource(id = R.color.text_lighter)) }
        },
        trailingIcon = trailingIcon?.let {
            { Icon(imageVector = it, contentDescription = null, tint = colorResource(id = R.color.text_lighter)) }
        },
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colorResource(id = R.color.text_lighter), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = colorResource(id = R.color.text_darker),
            unfocusedTextColor = colorResource(id = R.color.text_darker),
        ),
        singleLine = true
    )
}
