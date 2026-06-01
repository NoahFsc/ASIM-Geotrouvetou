package fr.miage.geotrouvetou.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.miage.geotrouvetou.R
import fr.miage.geotrouvetou.ui.components.atoms.Button
import fr.miage.geotrouvetou.ui.components.atoms.ButtonVariant
import fr.miage.geotrouvetou.ui.components.organisms.EventHistoryModal
import fr.miage.geotrouvetou.ui.components.organisms.EventListModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showHistory by remember { mutableStateOf(false) }
    var showEventList by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.navigateToLogin) {
        if (uiState.navigateToLogin) {
            onLogout()
            viewModel.onNavigationHandled()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Mon profil",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_darker),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Email",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(R.color.text_lighter),
            )
            Text(
                text = uiState.email.ifEmpty { "—" },
                fontSize = 16.sp,
                color = colorResource(R.color.text_darker),
            )
        }

        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = colorResource(R.color.danger_500),
                fontSize = 14.sp,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = colorResource(R.color.primary_500))
        } else {
            Button(
                text = "Voir les propositions",
                onClick = { showEventList = true },
                fullWidth = true,
                variant = ButtonVariant.Ghost,
            )
            Button(
                text = "Voir l'historique (Tempo)",
                onClick = { showHistory = true },
                fullWidth = true,
                variant = ButtonVariant.Ghost,
            )
            Button(
                text = "Se déconnecter",
                onClick = { viewModel.signOut() },
                fullWidth = true,
            )
            Button(
                text = "Supprimer le compte",
                onClick = { viewModel.deleteAccount() },
                fullWidth = true,
                variant = ButtonVariant.Ghost,
            )
        }
    }

    if (showHistory) {
        EventHistoryModal(
            onDismissRequest = { showHistory = false }
        )
    }

    if (showEventList) {
        EventListModal(
            onDismissRequest = { showEventList = false }
        )
    }
}
