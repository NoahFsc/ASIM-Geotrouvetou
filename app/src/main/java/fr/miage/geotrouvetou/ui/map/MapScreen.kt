package fr.miage.geotrouvetou.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.miage.geotrouvetou.R
import fr.miage.geotrouvetou.data.maps.OSMMapService
import fr.miage.geotrouvetou.domain.interfaces.MapBounds
import fr.miage.geotrouvetou.ui.components.atoms.RoundIconButton
import fr.miage.geotrouvetou.ui.components.organisms.EventListModal
import fr.miage.geotrouvetou.ui.components.organisms.SearchBar
import org.osmdroid.views.MapView

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
) {
    @Suppress("DEPRECATION")
    val context = LocalContext.current
    @Suppress("DEPRECATION")
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val mapService = remember(context) { OSMMapService(context) }
    val mapView = remember(context) { MapView(context) }

    var mapBound by rememberSaveable { mutableStateOf(false) }
    var locationPermissionGranted by rememberSaveable { mutableStateOf(hasLocationPermission(context)) }
    var locationFlowStarted by rememberSaveable { mutableStateOf(false) }
    var showEventList by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        locationPermissionGranted = granted
        viewModel.onLocationPermissionChanged(granted)
        if (!granted) {
            Toast.makeText(context, "Permission localisation refusée", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(mapView) {
        mapService.bind(mapView)
        mapService.setOnViewBoundsChangedListener { bounds: MapBounds ->
            viewModel.onViewBoundsChanged(bounds)
        }
        mapBound = true
        onDispose {
            mapService.onPause()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapService.onResume()
                Lifecycle.Event.ON_PAUSE -> mapService.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapService.onPause()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.startRealtime()
        if (!locationPermissionGranted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        } else {
            viewModel.onLocationPermissionChanged(true)
        }
    }

    LaunchedEffect(mapBound, locationPermissionGranted) {
        if (mapBound && locationPermissionGranted && !locationFlowStarted) {
            locationFlowStarted = true
            mapService.enableMyLocation { point ->
                val defaultZoom = mapService.getZoomForWidth(20.0, point.latitude)
                mapService.centerOn(point.latitude, point.longitude, defaultZoom)
                mapService.setMinimumZoomForWidth(20.0)
                viewModel.onFirstLocationFound(point.latitude, point.longitude)
            }
        }
    }

    LaunchedEffect(uiState.events) {
        mapService.displayEvents(uiState.events)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
    ) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
        )

        if (uiState.isLoading) {
            Text(text = "Chargement des événements…")
        }

        // Top-left: center on self
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp),
        ) {
            RoundIconButton(
                icon = Icons.Filled.MyLocation,
                contentDescription = "Centrer sur ma position",
                onClick = {
                    val loc = uiState.currentLocation ?: return@RoundIconButton
                    val zoom = mapService.getZoomForWidth(20.0, loc.first)
                    mapService.centerOn(loc.first, loc.second, zoom)
                },
            )
        }

        // Top-right: zoom in / zoom out
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RoundIconButton(
                icon = Icons.Filled.Add,
                contentDescription = "Zoom avant",
                onClick = { mapView.controller.zoomIn() },
            )
            RoundIconButton(
                icon = Icons.Filled.Remove,
                contentDescription = "Zoom arrière",
                onClick = { mapView.controller.zoomOut() },
            )
        }

        // Bottom-center: search bar opening the event list modal
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            SearchBar(
                value = "",
                onValueChange = {},
                placeholder = "Rechercher par lieu",
                onClick = { showEventList = true },
            )
        }
    }

    if (showEventList) {
        EventListModal(onDismissRequest = { showEventList = false })
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
}
