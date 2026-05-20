package fr.miage.geoevent.ui.map

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import fr.miage.geoevent.databinding.ActivityMainBinding
import fr.miage.geoevent.ui.auth.LoginActivity
import fr.miage.geoevent.ui.components.atoms.Toast
import fr.miage.geoevent.ui.events.CreateEventActivity
import fr.miage.geoevent.ui.profile.ProfileActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var toastKey by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupTestToast()

        binding.btnProfil.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnConnexion.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnCreateEvent.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        setupMap()
        checkLocationPermissions()
    }

    private fun setupTestToast() {
        binding.composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
        )

        binding.composeView.setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                if (toastKey > 0) {
                    Toast(
                        title = "Toast de test",
                        description = "Le composable Toast.kt s'affiche bien.",
                        key = toastKey,
                    )
                }
            }
        }

        binding.btnTestToast.setOnClickListener {
            toastKey++
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupMap() {
        // Lucas configurera OpenStreetMap ici
    }

    private fun checkLocationPermissions() {
        // Appel aux méthodes de Lucas pour la géolocalisation
    }
}
