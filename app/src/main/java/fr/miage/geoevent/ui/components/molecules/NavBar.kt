package fr.miage.geoevent.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.miage.geoevent.R
import fr.miage.geoevent.ui.components.atoms.TabElement

enum class NavTab { Carte, Ajouter, Profil }

@Composable
fun NavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(R.color.white))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TabElement(
            icon = Icons.Outlined.Map,
            label = "Carte",
            selected = selectedTab == NavTab.Carte,
            onClick = { onTabSelected(NavTab.Carte) },
        )
        TabElement(
            icon = Icons.Outlined.AddCircleOutline,
            label = "Ajouter",
            selected = selectedTab == NavTab.Ajouter,
            onClick = { onTabSelected(NavTab.Ajouter) },
        )
        TabElement(
            icon = Icons.Outlined.Person,
            label = "Profil",
            selected = selectedTab == NavTab.Profil,
            onClick = { onTabSelected(NavTab.Profil) },
        )
    }
}

@Preview(name = "NavBar – Carte active")
@Composable
private fun NavBarCartePreview() {
    NavBar(selectedTab = NavTab.Carte, onTabSelected = {})
}

@Preview(name = "NavBar – Ajouter active")
@Composable
private fun NavBarAjouterPreview() {
    NavBar(selectedTab = NavTab.Ajouter, onTabSelected = {})
}

@Preview(name = "NavBar – Profil active")
@Composable
private fun NavBarProfilPreview() {
    NavBar(selectedTab = NavTab.Profil, onTabSelected = {})
}
