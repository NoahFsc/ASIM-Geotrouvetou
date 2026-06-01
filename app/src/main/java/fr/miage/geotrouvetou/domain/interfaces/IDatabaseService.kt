package fr.miage.geotrouvetou.domain.interfaces

import fr.miage.geotrouvetou.domain.models.Evenement
import fr.miage.geotrouvetou.domain.models.User
import kotlinx.coroutines.flow.Flow

interface IDatabaseService {
    suspend fun addEvent(event: Evenement)
    suspend fun getAllEvents(): List<Evenement>
    fun listenToEventsRealtime(): Flow<List<Evenement>>
    suspend fun uploadImage(fileName: String, bytes: ByteArray): String
    suspend fun getProfile(userId: String): User?
    suspend fun createProfile(user: User)
    suspend fun deleteProfile(userId: String)
    suspend fun getEventsByVisibleBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): List<Evenement>
}
