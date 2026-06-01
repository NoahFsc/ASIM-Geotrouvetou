package fr.miage.geotrouvetou.ui.params

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilterHdr
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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

@Composable
fun ParamsScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ParamViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

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
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Retour
        Row(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onBackClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = colorResource(R.color.text_darker),
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "Retour",
                fontSize = 16.sp,
                color = colorResource(R.color.text_darker),
            )
        }

        // Titre
        Text(
            text = "Paramètres",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_darker),
        )

        // Section Compte
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Compte",
                fontSize = 14.sp,
                color = colorResource(R.color.text_lighter),
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val items = listOf(
                    Triple(Icons.Outlined.Edit, "Modifier mon profil", {}),
                    Triple(Icons.Outlined.Key, "Modifier mon mot de passe", {}),
                    Triple(Icons.Outlined.FilterHdr, "Modifier mes préférences", {}),
                    Triple(Icons.Outlined.Build, "Administration", {}),
                )
                items.forEach { (icon, label, action) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White),
                    ) {
                        Button(
                            text = label,
                            onClick = action,
                            leftIcon = icon,
                            rightIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            variant = ButtonVariant.GhostDark,
                            fullWidth = true,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Déconnexion
        Text(
            text = "Déconnexion",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.danger_500),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { viewModel.signOut() },
        )
    }
}
