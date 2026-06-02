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
data class NominatimAddress(
    @SerialName("house_number") val houseNumber: String? = null,
    val road: String? = null,
    val city: String? = null,
    val town: String? = null,
    val village: String? = null,
    val municipality: String? = null,
    val county: String? = null,   // département (ex: "Oise")
    val state: String? = null,    // région (ex: "Hauts-de-France")
    val postcode: String? = null,
    val country: String? = null,
)

@Serializable
data class NominatimPlace(
    @SerialName("display_name") val displayName: String,
    val lat: String,
    val lon: String,
    val address: NominatimAddress? = null,
) {
    val latitude: Double get() = lat.toDoubleOrNull() ?: 0.0
    val longitude: Double get() = lon.toDoubleOrNull() ?: 0.0

    /** "10 Rue de la Paix, Paris" ou "Paris" si pas de rue */
    val mainLine: String get() {
        val a = address ?: return displayName
        val locality = a.city ?: a.town ?: a.village ?: a.municipality
        val street = when {
            a.houseNumber != null && a.road != null -> "${a.houseNumber} ${a.road}"
            a.road != null -> a.road
            else -> null
        }
        return listOfNotNull(street, locality)
            .joinToString(", ")
            .ifBlank { displayName }
    }

    /** "75001, Oise, France" */
    val countryLine: String get() {
        val a = address ?: return ""
        return listOfNotNull(a.postcode, a.county, a.country)
            .joinToString(", ")
    }
}

object NominatimService {
    private val client = HttpClient(OkHttp)
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun search(query: String, limit: Int = 8): List<NominatimPlace> {
        if (query.isBlank()) return emptyList()
        val text = client.get("https://nominatim.openstreetmap.org/search") {
            parameter("q", query)
            parameter("format", "json")
            parameter("limit", limit)
            parameter("addressdetails", 1)
            header("User-Agent", "ASIM-Geoevent/1.0")
        }.bodyAsText()

        val results: List<NominatimPlace> = json.decodeFromString(text)

        // Déduplique par texte affiché : deux résultats qui produisent
        // la même ligne principale (même ville + code postal) sont identiques
        return results.distinctBy { it.mainLine }.take(5)
    }
}
