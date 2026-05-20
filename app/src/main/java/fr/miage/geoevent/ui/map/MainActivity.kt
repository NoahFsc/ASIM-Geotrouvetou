package fr.miage.geoevent.ui.map

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import fr.miage.geoevent.R
import fr.miage.geoevent.databinding.ActivityMainBinding
import fr.miage.geoevent.ui.components.atoms.BadgeStatus
import fr.miage.geoevent.ui.components.atoms.Button
import fr.miage.geoevent.ui.components.atoms.ButtonVariant
import fr.miage.geoevent.ui.components.atoms.Checkbox
import fr.miage.geoevent.ui.components.atoms.Input
import fr.miage.geoevent.ui.components.atoms.NavCard
import fr.miage.geoevent.ui.components.atoms.RadioButton
import fr.miage.geoevent.ui.components.atoms.RoundIconButton
import fr.miage.geoevent.ui.components.atoms.Slider
import fr.miage.geoevent.ui.components.atoms.StatusBadge
import fr.miage.geoevent.ui.components.atoms.StatusTag
import fr.miage.geoevent.ui.components.atoms.Switch
import fr.miage.geoevent.ui.components.atoms.TabElement
import fr.miage.geoevent.ui.components.atoms.TagStatus
import fr.miage.geoevent.ui.components.molecules.EventCard
import fr.miage.geoevent.ui.components.molecules.EventDetailCard
import fr.miage.geoevent.ui.components.molecules.NavBar
import fr.miage.geoevent.ui.components.molecules.NavTab
import fr.miage.geoevent.ui.components.molecules.ProfileCard
import fr.miage.geoevent.ui.components.molecules.Select

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupShowcase()
        setupNavBar()
        setupMap()
        checkLocationPermissions()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupShowcase() {
        binding.showcase.setContent {
            MaterialTheme {
                var checked by remember { mutableStateOf(false) }
                var radioSelected by remember { mutableStateOf(false) }
                var switchOn by remember { mutableStateOf(false) }
                var sliderValue by remember { mutableFloatStateOf(0.4f) }
                var inputValue by remember { mutableStateOf("") }
                var selectValue by remember { mutableStateOf<String?>(null) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(R.color.background))
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Section("Buttons")
                    Button(text = "Fill default", onClick = {})
                    Button(text = "Fill disabled", onClick = {}, enabled = false)
                    Button(text = "Ghost", onClick = {}, variant = ButtonVariant.Ghost)
                    Button(text = "Avec icône", onClick = {}, leftIcon = Icons.Outlined.Edit)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RoundIconButton(icon = Icons.Outlined.Tune, onClick = {})
                        RoundIconButton(icon = Icons.Outlined.Lock, onClick = {})
                    }

                    HorizontalDivider()

                    Section("Input")
                    Input(value = inputValue, onValueChange = { inputValue = it }, placeholder = "Saisir du texte")

                    HorizontalDivider()

                    Section("Select")
                    Select(
                        value = selectValue,
                        onValueChange = { selectValue = it },
                        options = listOf("Option A", "Option B", "Option C"),
                        placeholder = "Choisir une option",
                    )

                    HorizontalDivider()

                    Section("Checkbox · Radio · Switch")
                    Checkbox(checked = checked, onCheckedChange = { checked = it }, label = "J'accepte les conditions")
                    RadioButton(selected = radioSelected, onClick = { radioSelected = !radioSelected }, label = "Option sélectionnable")
                    Switch(checked = switchOn, onCheckedChange = { switchOn = it }, label = "Activer les notifications")

                    HorizontalDivider()

                    Section("Slider")
                    Slider(value = sliderValue, onValueChange = { sliderValue = it })

                    HorizontalDivider()

                    Section("Badges")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusBadge(status = BadgeStatus.VALIDATED)
                        StatusBadge(status = BadgeStatus.WAITING)
                        StatusBadge(status = BadgeStatus.REFUSED)
                    }
                    Section("Tags")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusTag(status = TagStatus.NEW)
                        StatusTag(status = TagStatus.SOON)
                        StatusTag(status = TagStatus.DONE)
                    }

                    HorizontalDivider()

                    Section("NavCard")
                    NavCard(icon = Icons.Outlined.Edit, label = "Modifier mon profil", onClick = {})
                    NavCard(icon = Icons.Outlined.Lock, label = "Changer le mot de passe", onClick = {})

                    HorizontalDivider()

                    Section("TabElement")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TabElement(icon = Icons.Outlined.Edit, label = "Inactif", selected = false, onClick = {})
                        TabElement(icon = Icons.Outlined.Edit, label = "Actif", selected = true, onClick = {})
                    }

                    HorizontalDivider()

                    Section("EventCard")
                    EventCard(
                        tag = TagStatus.NEW,
                        title = "Forêt d'Ornans",
                        date = "28/04",
                        time = "09:00",
                        isRecommended = true,
                        attendance = "Faible",
                        onClick = {},
                    )
                    EventCard(
                        tag = TagStatus.NEW,
                        title = "Forêt d'Ornans",
                        date = "28/04",
                        time = "09:00",
                        onClick = {},
                        onDelete = {},
                    )

                    HorizontalDivider()

                    Section("EventDetailCard")
                    EventDetailCard(
                        date = "Samedi, 28 Avril",
                        time = "09:00",
                        locationName = "Forêt de Chailluz",
                        locationDetail = "Besançon, France",
                    )

                    HorizontalDivider()

                    Section("ProfileCard")
                    ProfileCard(
                        name = "Alex Dubois",
                        username = "alex_dubois",
                        onInvite = {},
                    )
                }
            }
        }
    }

    private fun setupNavBar() {
        var selectedTab by mutableStateOf(NavTab.Carte)
        binding.navBar.setContent {
            MaterialTheme {
                NavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab -> selectedTab = tab },
                )
            }
        }
    }

    private fun setupMap() {
        // Lucas configurera OpenStreetMap ici
    }

    private fun checkLocationPermissions() {
        // Appel aux méthodes de Lucas pour la géolocalisation
    }
}

@androidx.compose.runtime.Composable
private fun Section(title: String) {
    Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
}
