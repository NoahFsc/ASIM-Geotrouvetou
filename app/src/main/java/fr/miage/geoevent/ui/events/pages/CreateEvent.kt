package fr.miage.geoevent.ui.events.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.miage.geoevent.ui.events.components.AppButton

@Composable
fun CreateEvent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Nouvel événement",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Ici tu pourras créer ton événement."
        )

        AppButton(
            text = "Modifier le mot de passe",
            onClick = {
                println("Bouton cliqué")
            },
            enabled = true
        )

        AppButton(
            text = "Modifier le mot de passe",
            onClick = {
                println("Bouton cliqué")
            },
            enabled = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateEventPreview() {
    CreateEvent()
}
