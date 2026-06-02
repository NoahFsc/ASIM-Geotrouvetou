package fr.miage.geotrouvetou.ui.utils

import fr.miage.geotrouvetou.domain.models.Evenement
import fr.miage.geotrouvetou.ui.components.atoms.TagStatus
import java.time.LocalDate

/** Dérive le statut d'affichage à partir de la date de l'événement. */
fun Evenement.tagStatus(): TagStatus {
    val dateStr = event_date ?: return TagStatus.NEW
    return try {
        val eventDate = LocalDate.parse(dateStr.substringBefore('T'))
        val today = LocalDate.now()
        when {
            eventDate.isBefore(today) -> TagStatus.DONE
            eventDate.isBefore(today.plusDays(7)) -> TagStatus.SOON
            else -> TagStatus.NEW
        }
    } catch (e: Exception) {
        TagStatus.NEW
    }
}

/** "2024-04-28" ou "—" si pas de date. */
fun Evenement.formattedDate(): String = event_date?.substringBefore('T') ?: "—"

/** "09:00" ou "—" si pas d'heure. */
fun Evenement.formattedTime(): String =
    event_date?.let { if ('T' in it) it.substringAfter('T').take(5) else null } ?: "—"
