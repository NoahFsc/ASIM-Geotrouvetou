package fr.miage.geotrouvetou.domain.interfaces

import fr.miage.geotrouvetou.domain.models.AdminStats
import fr.miage.geotrouvetou.domain.models.AuditLogEntry
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
    suspend fun updateProfile(userId: String, fullName: String)
    suspend fun updateAvatar(userId: String, bytes: ByteArray)
    suspend fun deleteProfile(userId: String)
    suspend fun getEventsByVisibleBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): List<Evenement>

    // Admin — stats & listes
    suspend fun getAdminStats(): AdminStats
    suspend fun getAdminUsers(page: Int = 0, pageSize: Int = 20): List<User>
    suspend fun getAdminEvents(page: Int = 0, pageSize: Int = 20): List<Evenement>
    suspend fun getRecentActivity(limit: Int = 10): List<AuditLogEntry>

    // Admin — actions
    suspend fun updateUserRole(userId: String, role: String)
    suspend fun adminDeleteUser(userId: String)
    suspend fun deleteEvent(eventId: String)
}
