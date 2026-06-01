package fr.miage.geotrouvetou.ui.auth.modals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geotrouvetou.R
import fr.miage.geotrouvetou.ui.components.organisms.Modal

private val requirements = listOf(
    "Minimum 8 caractères",
    "1 Chiffre",
    "1 Caractère Spécial (ex : @, &,...)",
    "1 Majuscule",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInfoModal(onDismiss: () -> Unit) {
    Modal(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Votre mot de passe doit contenir :",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.text_darker),
            )
            requirements.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = colorResource(R.color.text_lighter),
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = item,
                        fontSize = 16.sp,
                        color = colorResource(R.color.text_darker),
                    )
                }
            }
        }
    }
}
