package fr.miage.geotrouvetou.data.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import androidx.core.content.ContextCompat
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.TypedValue
import android.os.Looper
import android.util.Log
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.drawToBitmap
import android.view.ViewGroup
import fr.miage.geotrouvetou.domain.interfaces.IMapService
import fr.miage.geotrouvetou.domain.interfaces.MapBounds
import fr.miage.geotrouvetou.domain.models.Evenement
import fr.miage.geotrouvetou.ui.components.atoms.MarkerIcon
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.clustering.StaticCluster
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.max

class OSMMapService(private val context: Context) : IMapService {

    private lateinit var mapView: MapView
    private lateinit var controller: IMapController
    private var myLocationOverlay: MyLocationNewOverlay? = null
    private var clusterOverlay: RadiusMarkerClusterer? = null
    private var onEventClick: ((Evenement) -> Unit)? = null
    private var onClusterClick: ((List<Evenement>) -> Unit)? = null
    private var cachedMarkerIcon: BitmapDrawable? = null
    private var cachedClusterIcon: Bitmap? = null
    private var onViewBoundsChanged: ((MapBounds) -> Unit)? = null
    private var minimumWidthKm = 20.0
    private var minimumZoomNeedsInitialization = true
    private var minimumZoomLevel = Double.NaN
    private var isAdjustingZoom = false
    private var displayGeneration = 0
    private var lastNotifiedBounds: MapBounds? = null

    init {
        Configuration.getInstance().load(
            context.applicationContext,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = context.packageName
    }

    override fun bind(mapView: MapView) {
        this.mapView = mapView
        controller = mapView.controller
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        controller.setZoom(3.0)
        controller.setCenter(GeoPoint(20.0, 0.0))

        clusterOverlay = buildClusterOverlay()
        mapView.overlays.add(clusterOverlay)

        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
            isDrawAccuracyEnabled = true
        }
        mapView.overlays.add(myLocationOverlay)
        mapView.setBuiltInZoomControls(false)

        mapView.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                refreshMinimumZoomLevel()
                enforceMinimumZoom()
                notifyBoundsChanged()
                return false
            }
            override fun onZoom(event: ZoomEvent?): Boolean {
                refreshMinimumZoomLevel()
                enforceMinimumZoom()
                notifyBoundsChanged()
                return false
            }
        })

        mapView.post {
            refreshMinimumZoomLevel(force = true)
            enforceMinimumZoom()
            notifyBoundsChanged()
        }

        // Ne pas pré-générer ici: si la vue n'est pas encore attachée, Compose peut échouer
        // et mettre en cache un fallback sans drapeau.
    }

    override fun onResume() {
        if (this::mapView.isInitialized) {
            mapView.onResume()
        }
        if (hasLocationPermission()) {
            myLocationOverlay?.enableMyLocation()
            myLocationOverlay?.enableFollowLocation()
        }
    }

    override fun onPause() {
        if (this::mapView.isInitialized) {
            mapView.onPause()
        }
        myLocationOverlay?.disableMyLocation()
    }

    override fun enableMyLocation(onFirstFix: ((Evenement) -> Unit)?) {
        if (!hasLocationPermission()) return

        val overlay = myLocationOverlay ?: return
        overlay.enableMyLocation()
        overlay.enableFollowLocation()

        if (onFirstFix != null) {
            overlay.runOnFirstFix {
                mapView.post {
                    overlay.myLocation?.let { location ->
                        onFirstFix(
                            Evenement(
                                id = "my-location",
                                title = "",
                                description = "",
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                        )
                    }
                }
            }
        }
    }

    override fun disableMyLocation() {
        myLocationOverlay?.disableMyLocation()
    }

    override fun centerOn(latitude: Double, longitude: Double, zoom: Double) {
        if (!this::mapView.isInitialized) return
        val geoPoint = GeoPoint(latitude, longitude)
        val targetZoom = if (minimumZoomLevel.isFinite()) max(zoom, minimumZoomLevel) else zoom
        controller.setZoom(targetZoom)
        controller.setCenter(geoPoint)
        controller.animateTo(geoPoint)
    }

    override fun getZoomForWidth(widthKm: Double, latitude: Double): Double {
        return calculateZoomForWidth(widthKm, latitude)
    }

    override fun addMarker(event: Evenement) {
        if (!this::mapView.isInitialized) return
        mapView.post {
            addMarkerInternal(event)
            clusterOverlay?.invalidate()
            mapView.invalidate()
        }
    }

    override fun displayEvents(events: List<Evenement>) {
        if (!this::mapView.isInitialized) return
        val snapshot = events.toList()
        val generation = ++displayGeneration
        mapView.post {
            if (generation != displayGeneration) return@post

            // Remplace le cluster overlay existant par un nouveau (osmbonuspack n'expose pas de clear())
            mapView.overlays.removeAll { it is RadiusMarkerClusterer }
            val newCluster = buildClusterOverlay()
            clusterOverlay = newCluster

            snapshot.forEach { event -> addMarkerInternal(event) }

            // Insérer à l'index 0 pour que myLocationOverlay reste au-dessus
            mapView.overlays.add(0, newCluster)
            newCluster.invalidate()
            mapView.invalidate()
        }
    }

    private fun addMarkerInternal(event: Evenement) {
        val cluster = clusterOverlay ?: return
        val marker = Marker(mapView).apply {
            position = GeoPoint(event.latitude, event.longitude)
            // L'icône est circulaire, donc on la centre pour éviter un décalage visuel sur les bords.
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = getMarkerIcon()
            icon?.let { d ->
                if (d.intrinsicWidth > 0 && d.intrinsicHeight > 0) {
                    d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
                }
            }
            title = event.title
            snippet = event.description
            relatedObject = event
            infoWindow = null
            setOnMarkerClickListener { _, _ ->
                onEventClick?.invoke(event)
                true
            }
        }
        Log.d("OSMMapService", "Adding marker at ${event.latitude}, ${event.longitude} with title='${event.title}'")
        cluster.add(marker)
    }

    private fun buildClusterOverlay(): RadiusMarkerClusterer = ClusterMarkerOverlay().apply {
        setRadius(100)
        setIcon(getClusterIcon())
    }

    private inner class ClusterMarkerOverlay : RadiusMarkerClusterer(context) {
        override fun buildClusterMarker(cluster: StaticCluster, mapView: MapView): Marker {
            val marker = super.buildClusterMarker(cluster, mapView)
            marker.setOnMarkerClickListener { _, _ ->
                val events = (0 until cluster.size)
                    .mapNotNull { cluster.getItem(it).relatedObject as? Evenement }
                if (events.isNotEmpty()) onClusterClick?.invoke(events)
                true
            }
            return marker
        }
    }

    override fun setOnEventClickListener(listener: ((Evenement) -> Unit)?) {
        onEventClick = listener
    }

    override fun setOnClusterClickListener(listener: ((List<Evenement>) -> Unit)?) {
        onClusterClick = listener
    }

    private fun getClusterIcon(): Bitmap {
        cachedClusterIcon?.let { return it }
        val sizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 48f, context.resources.displayMetrics
        ).toInt()
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val center = sizePx / 2f
        val stroke = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics)
        val radius = center - stroke
        canvas.drawCircle(center, center, radius, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = context.getColor(fr.miage.geotrouvetou.R.color.primary_400)
        })
        canvas.drawCircle(center, center, radius, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.WHITE
            strokeWidth = stroke
        })
        return bitmap.also { cachedClusterIcon = it }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    private fun getMarkerIcon(): BitmapDrawable {
        cachedMarkerIcon?.let { return it }
        val composeIcon = createMarkerIcon()
        if (composeIcon != null) {
            cachedMarkerIcon = composeIcon
            Log.d("OSMMapService", "Marker icon rendered from Compose MarkerIcon")
            return composeIcon
        }
        Log.w("OSMMapService", "Using temporary fallback icon (compose not ready yet)")
        return createFallbackIcon()
    }

    // Crée une icône Compose; renvoie null si le rendu Compose n'est pas prêt/possible.
    private fun createMarkerIcon(): BitmapDrawable? {
        // Ensure we run UI operations on the main thread
        if (Looper.myLooper() != Looper.getMainLooper()) {
            val latch = java.util.concurrent.CountDownLatch(1)
            var result: BitmapDrawable? = null
            android.os.Handler(Looper.getMainLooper()).post {
                try {
                    result = createMarkerIconInternal()
                } finally {
                    latch.countDown()
                }
            }
            latch.await()
            return result
        }
        return createMarkerIconInternal()
    }

    private fun createMarkerIconInternal(): BitmapDrawable? {
        val sizeDp = 48
        val sizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            sizeDp.toFloat(),
            context.resources.displayMetrics
        ).toInt()

        if (!mapView.isAttachedToWindow) {
            Log.d("OSMMapService", "Compose marker icon skipped: mapView not attached yet")
            return null
        }

        return try {
            val composeView = ComposeView(context).apply {
                setContent {
                    MarkerIcon(size = sizeDp.dp, borderWidth = 3.dp)
                }
            }

            // Attach temporarily to the same view tree to inherit lifecycle owners used by Compose.
            val parent = mapView.parent as? ViewGroup
            if (parent != null) {
                val lp = ViewGroup.LayoutParams(sizePx, sizePx)
                parent.addView(composeView, lp)
            }

            val bitmap = try {
                val spec = android.view.View.MeasureSpec.makeMeasureSpec(sizePx, android.view.View.MeasureSpec.EXACTLY)
                composeView.measure(spec, spec)
                composeView.layout(0, 0, sizePx, sizePx)
                composeView.drawToBitmap(Bitmap.Config.ARGB_8888)
            } finally {
                if (parent != null) {
                    parent.removeView(composeView)
                }
            }

            BitmapDrawable(context.resources, bitmap).also {
                it.setBounds(0, 0, bitmap.width, bitmap.height)
            }
        } catch (t: Throwable) {
            Log.w("OSMMapService", "Compose icon rendering failed (MarkerIcon)", t)
            null
        }
    }

    private fun createFallbackIcon(): BitmapDrawable {
        val sizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            48f,
            context.resources.displayMetrics
        ).toInt()
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val center = sizePx / 2f
        val stroke = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics)
        val radius = center - stroke
        val fill = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            style = android.graphics.Paint.Style.FILL
            color = context.getColor(fr.miage.geotrouvetou.R.color.primary_400)
        }
        val border = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            style = android.graphics.Paint.Style.STROKE
            color = android.graphics.Color.WHITE
            strokeWidth = stroke
        }
        val flagPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            style = android.graphics.Paint.Style.FILL
            color = context.getColor(fr.miage.geotrouvetou.R.color.primary_600)
        }
        val flagStemPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            style = android.graphics.Paint.Style.STROKE
            color = android.graphics.Color.WHITE
            strokeWidth = stroke * 0.8f
            strokeCap = android.graphics.Paint.Cap.ROUND
        }
        val flagPath = android.graphics.Path().apply {
            moveTo(center - sizePx * 0.10f, center - sizePx * 0.18f)
            lineTo(center + sizePx * 0.12f, center - sizePx * 0.10f)
            lineTo(center - sizePx * 0.03f, center + sizePx * 0.02f)
            close()
        }
        canvas.drawCircle(center, center, radius, fill)
        canvas.drawPath(flagPath, flagPaint)
        canvas.drawLine(
            center - sizePx * 0.05f,
            center - sizePx * 0.20f,
            center - sizePx * 0.05f,
            center + sizePx * 0.12f,
            flagStemPaint,
        )
        canvas.drawCircle(center, center, radius, border)
        return BitmapDrawable(context.resources, bitmap).also {
            it.setBounds(0, 0, bitmap.width, bitmap.height)
        }
    }

    override fun getVisibleBounds(): MapBounds? {
        if (!this::mapView.isInitialized) return null

        val width = mapView.width
        val height = mapView.height

        if (width <= 0 || height <= 0) {
            Log.w("OSMMapService", "getVisibleBounds: invalid dimensions width=$width height=$height")
            return null
        }

        // Convertir les quatre coins de l'écran en coordonnées géographiques
        val topLeftGeo = mapView.projection.fromPixels(0, 0) as? GeoPoint
        val topRightGeo = mapView.projection.fromPixels(width, 0) as? GeoPoint
        val bottomLeftGeo = mapView.projection.fromPixels(0, height) as? GeoPoint
        val bottomRightGeo = mapView.projection.fromPixels(width, height) as? GeoPoint

        if (topLeftGeo == null || topRightGeo == null || bottomLeftGeo == null || bottomRightGeo == null) {
            Log.w("OSMMapService", "getVisibleBounds: some projection failed")
            return null
        }

        // Extraire les limites lat/lon des quatre coins
        val latitudes = listOf(topLeftGeo.latitude, topRightGeo.latitude, bottomLeftGeo.latitude, bottomRightGeo.latitude)
        val longitudes = listOf(topLeftGeo.longitude, topRightGeo.longitude, bottomLeftGeo.longitude, bottomRightGeo.longitude)

        val bounds = MapBounds(
            minLat = latitudes.minOrNull() ?: return null,
            maxLat = latitudes.maxOrNull() ?: return null,
            minLon = longitudes.minOrNull() ?: return null,
            maxLon = longitudes.maxOrNull() ?: return null
        )

        Log.d("OSMMapService", "getVisibleBounds: mapView.width=$width, mapView.height=$height, " +
                "bounds=[${String.format("%.4f", bounds.minLat)}..${String.format("%.4f", bounds.maxLat)}] x " +
                "[${String.format("%.4f", bounds.minLon)}..${String.format("%.4f", bounds.maxLon)}]")

        return bounds
    }

    override fun setOnViewBoundsChangedListener(listener: ((MapBounds) -> Unit)?) {
        onViewBoundsChanged = listener
    }

    override fun setMinimumZoomForWidth(widthKm: Double) {
        minimumWidthKm = widthKm
        minimumZoomNeedsInitialization = true
        mapView.post {
            refreshMinimumZoomLevel(force = true)
            enforceMinimumZoom()
        }
    }

    private fun enforceMinimumZoom() {
        if (!minimumZoomLevel.isFinite() || isAdjustingZoom) return
        val currentZoom = mapView.zoomLevel.toDouble()
        if (currentZoom + 0.0001 < minimumZoomLevel) {
            isAdjustingZoom = true
            try {
                controller.setZoom(minimumZoomLevel)
                Log.d(
                    "OSMMapService",
                    "enforceMinimumZoom: currentZoom=${String.format("%.2f", currentZoom)}, minimumZoomLevel=${String.format("%.2f", minimumZoomLevel)} -> zooming in"
                )
            } finally {
                isAdjustingZoom = false
            }
        }
    }

    private fun refreshMinimumZoomLevel(force: Boolean = false) {
        if (!force && !minimumZoomNeedsInitialization && minimumZoomLevel.isFinite()) return
        val bounds = getVisibleBounds() ?: return
        val centerLat = (bounds.minLat + bounds.maxLat) / 2.0
        minimumZoomLevel = calculateZoomForWidth(minimumWidthKm, centerLat)
        minimumZoomNeedsInitialization = false
        val currentZoom = if (this::mapView.isInitialized) mapView.zoomLevel.toDouble() else Double.NaN
        Log.d(
            "OSMMapService",
            "refreshMinimumZoomLevel: centerLat=${String.format("%.4f", centerLat)}, widthKm=$minimumWidthKm, minimumZoomLevel=${String.format("%.2f", minimumZoomLevel)}, currentZoom=${String.format("%.2f", currentZoom)}"
        )
    }

    private fun notifyBoundsChanged() {
        val bounds = getVisibleBounds() ?: return
        if (lastNotifiedBounds != null && !hasSignificantBoundsChange(lastNotifiedBounds!!, bounds)) return
        val widthKm = calculateDistanceKm(bounds.minLat, bounds.minLon, bounds.minLat, bounds.maxLon)
        val currentZoom = mapView.zoomLevel.toDouble()
        Log.d("OSMMapService", "notifyBoundsChanged: currentZoom=${String.format("%.2f", currentZoom)}, " +
                "visibleWidth=${String.format("%.2f", widthKm)}km (min=$minimumWidthKm km), " +
                "bounds=[${String.format("%.4f", bounds.minLat)}..${String.format("%.4f", bounds.maxLat)}] x " +
                "[${String.format("%.4f", bounds.minLon)}..${String.format("%.4f", bounds.maxLon)}]")
        if (lastNotifiedBounds == null || bounds != lastNotifiedBounds) {
            lastNotifiedBounds = bounds
            onViewBoundsChanged?.invoke(bounds)
        }
    }

    private fun hasSignificantBoundsChange(previous: MapBounds, current: MapBounds): Boolean {
        val epsilon = 1e-4
        return abs(previous.minLat - current.minLat) > epsilon ||
                abs(previous.maxLat - current.maxLat) > epsilon ||
                abs(previous.minLon - current.minLon) > epsilon ||
                abs(previous.maxLon - current.maxLon) > epsilon
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.asin(Math.sqrt(a))
        return earthRadiusKm * c
    }

    private fun calculateZoomForWidth(widthKm: Double, centerLat: Double): Double {
        val viewWidthPx = getViewWidthPx().coerceAtLeast(1).toDouble()
        val widthMeters = widthKm * 1000.0
        val latFactor = abs(Math.cos(Math.toRadians(centerLat))).coerceAtLeast(1e-6)
        val metersPerPixelAtZoom0 = (2.0 * Math.PI * 6378137.0 * latFactor) / 256.0
        val zoom = ln(viewWidthPx * metersPerPixelAtZoom0 / widthMeters) / ln(2.0)
        Log.d(
            "OSMMapService",
            "calculateZoomForWidth: widthKm=$widthKm, centerLat=$centerLat, viewWidthPx=$viewWidthPx -> zoom=${String.format("%.2f", zoom)}"
        )
        return zoom
    }

    private fun getViewWidthPx(): Int {
        return if (this::mapView.isInitialized && mapView.width > 0) {
            mapView.width
        } else {
            context.resources.displayMetrics.widthPixels
        }
    }
}