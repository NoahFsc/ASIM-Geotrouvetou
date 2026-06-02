package fr.miage.geotrouvetou.data.geocoding

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class NominatimPlace(
    @SerialName("display_name") val displayName: String,
    val lat: String,
    val lon: String
) {
    val latitude: Double get() = lat.toDoubleOrNull() ?: 0.0
    val longitude: Double get() = lon.toDoubleOrNull() ?: 0.0
}

object NominatimService {
    private val client = HttpClient(OkHttp)
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun search(query: String, limit: Int = 5): List<NominatimPlace> {
        if (query.isBlank()) return emptyList()
        val text = client.get("https://nominatim.openstreetmap.org/search") {
            parameter("q", query)
            parameter("format", "json")
            parameter("limit", limit)
            header("User-Agent", "ASIM-Geoevent/1.0")
        }.bodyAsText()
        return json.decodeFromString(text)
    }
}
