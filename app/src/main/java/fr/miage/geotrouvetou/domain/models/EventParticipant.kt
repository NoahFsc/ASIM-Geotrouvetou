package fr.miage.geotrouvetou.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class EventParticipant(
    val event_id: String,
    val profile_id: String
)
