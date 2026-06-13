package fr.miage.geotrouvetou.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Evenement(
    val id: String? = null,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val location: String? = null,
    val image_url: String? = null,
    val user_id: String? = null,
    val event_date: String? = null,
    // created_at est géré automatiquement par la BDD
    val created_at: String? = null,
    // true = public, false = privé (owner only)
    val visibility: Boolean = true,
)
