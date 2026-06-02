package fr.miage.geotrouvetou.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class AuditLogEntry(
    val id: String = "",
    val action: String,
    @SerialName("actor_id") val actorId: String? = null,
    @SerialName("target_id") val targetId: String? = null,
    @SerialName("target_name") val targetName: String? = null,
    val metadata: JsonObject? = null,
    @SerialName("created_at") val createdAt: String? = null,
)
