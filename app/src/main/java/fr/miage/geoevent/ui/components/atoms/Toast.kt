package fr.miage.geoevent.ui.components.atoms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geoevent.R

@Composable
fun Toast(
    title: String = "Succès !",
    description: String = "Description du toast",
    modifier: Modifier = Modifier,
    duration: Int = 3000,
    initialProgress: Float = 0f,
    key: Any = Unit // Ajout d'une clé pour forcer le redémarrage
) {
    var isVisible by remember { mutableStateOf(false) }
    val progress = remember { Animatable(initialProgress) }

    LaunchedEffect(key) { // On écoute la clé pour relancer
        if (duration > 0) {
            progress.snapTo(0f) // On remet la barre à zéro
            isVisible = true
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = duration,
                    easing = LinearEasing
                )
            )
            isVisible = false
        } else {
            isVisible = true
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .statusBarsPadding() // AJOUT : Évite la caméra et la barre de statut
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = colorResource(id = R.color.success_transparent),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Succès",
                        tint = colorResource(id = R.color.success_500),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        color = colorResource(id = R.color.text_darker),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = description,
                        color = colorResource(id = R.color.text_light),
                        fontSize = 17.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(colorResource(id = R.color.success_transparent))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.value)
                        .fillMaxHeight()
                        .background(colorResource(id = R.color.success_500))
                )
            }
        }
    }
}

@Preview(name = "Toast 0% (Début)", showBackground = true)
@Composable
fun ToastPreview0() {
    Toast(title = "Succès !", description = "Voici la notif toast", duration = 0, initialProgress = 0f, modifier = Modifier.padding(16.dp))
}

@Preview(name = "Toast Animé (3s)", showBackground = true)
@Composable
fun ToastPreviewAnimated() {
    Toast(title = "Succès !", description = "Voici la notif toast", duration = 3000, modifier = Modifier.padding(16.dp))
}
