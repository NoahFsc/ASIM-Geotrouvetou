package fr.miage.geoevent.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.miage.geoevent.R

@Composable
fun RoundIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp = 56.dp,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(size),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = colorResource(R.color.white),
            contentColor = colorResource(R.color.primary_600),
        ),
        shape = CircleShape,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(size * 0.45f),
        )
    }
}

@Preview(name = "RoundIconButton")
@Composable
private fun RoundIconButtonPreview() {
    Box(
        modifier = Modifier
            .background(colorResource(R.color.text_darker))
            .padding(24.dp)
    ) {
        RoundIconButton(icon = Icons.Filled.Tune, onClick = {})
    }
}
