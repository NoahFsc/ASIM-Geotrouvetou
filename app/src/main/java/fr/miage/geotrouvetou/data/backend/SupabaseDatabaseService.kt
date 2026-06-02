package fr.miage.geotrouvetou.data.backend

import fr.miage.geotrouvetou.domain.interfaces.IDatabaseService
import fr.miage.geotrouvetou.domain.models.AdminStats
import fr.miage.geotrouvetou.domain.models.AuditLogEntry
import fr.miage.geotrouvetou.domain.models.Evenement
import fr.miage.geotrouvetou.domain.models.User
import io.github.jan.supabase.postgrest.query.Order
import fr.miage.geotrouvetou.utils.ImageHelper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Implémentation du service de données via Supabase.
 * Gère les interactions avec la base Postgrest, le Storage et le Realtime.
 */
class SupabaseDatabaseService(private val client: SupabaseClient) : IDatabaseService {

    private val tableName = "events"
    private val imageHelper = ImageHelper(client)

    override suspend fun addEvent(event: Evenement) {
        client.postgrest[tableName].insert(event)
    }

    /**
     * Utilise le helper dédié pour uploader une image et récupérer son lien public.
     */
    override suspend fun uploadImage(fileName: String, bytes: ByteArray): String {
        return imageHelper.uploadEventImage(fileName, bytes)
    }

    override suspend fun getAllEvents(): List<Evenement> {
        return client.postgrest[tableName].select().decodeList<Evenement>()
    }

    override suspend fun getEventsByVisibleBounds(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): List<Evenement> {
        return client.postgrest[tableName]
            .select {
                filter {
                    gte("latitude", minLat)
                    lte("latitude", maxLat)
                    gte("longitude", minLon)
                    lte("longitude", maxLon)
                }
            }
            .decodeList<Evenement>()
    }

    /**
     * Ouvre un canal de communication temps réel pour écouter les modifications de la table 'events'.
     */
    override fun listenToEventsRealtime(): Flow<List<Evenement>> {
        val channel = client.realtime.channel("public-events")

        return channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = tableName
        }.map {
            // Recharge la liste complète à chaque modification pour assurer la cohérence
            getAllEvents()
        }.onStart {
            channel.subscribe()
            emit(getAllEvents()) // Émet la liste initiale dès l'abonnement
        }
    }

    override suspend fun getProfile(userId: String): User? {
        return client.postgrest["profiles"].select {
            filter { eq("id", userId) }
        }.decodeSingleOrNull<User>()
    }

    override suspend fun createProfile(user: User) {
        client.postgrest["profiles"].insert(user)
    }

    override suspend fun deleteProfile(userId: String) {
        client.postgrest["profiles"].delete {
            filter {
                eq("id", userId)
            }
        }
        // Supprime le compte dans auth.users via une fonction SQL (security definer)
        client.postgrest.rpc("delete_own_account")
    }

    // ── Admin ────────────────────────────────────────────────────────────────

    override suspend fun getAdminStats(): AdminStats {
        val userCount = client.postgrest["profiles"].select().decodeList<User>().size
        val eventCount = client.postgrest[tableName].select().decodeList<Evenement>().size
        return AdminStats(userCount = userCount, eventCount = eventCount)
    }

    override suspend fun getAdminUsers(page: Int, pageSize: Int): List<User> {
        val from = (page * pageSize).toLong()
        val to = ((page + 1) * pageSize - 1).toLong()
        return client.postgrest["profiles"]
            .select { range(from = from, to = to) }
            .decodeList<User>()
    }

    override suspend fun getAdminEvents(page: Int, pageSize: Int): List<Evenement> {
        val from = (page * pageSize).toLong()
        val to = ((page + 1) * pageSize - 1).toLong()
        return client.postgrest[tableName]
            .select {
                range(from = from, to = to)
                order(column = "created_at", order = Order.DESCENDING)
            }
            .decodeList<Evenement>()
    }

    override suspend fun getRecentActivity(limit: Int): List<AuditLogEntry> {
        return client.postgrest["audit_log"]
            .select {
                order(column = "created_at", order = Order.DESCENDING)
                this.limit(count = limit.toLong())
            }
            .decodeList<AuditLogEntry>()
    }

    override suspend fun updateUserRole(userId: String, role: String) {
        client.postgrest["profiles"].update(
            update = { set("role", role) },
            request = { filter { eq("id", userId) } },
        )
    }

    override suspend fun adminDeleteUser(userId: String) {
        client.postgrest.rpc("admin_delete_user", mapOf("target_user_id" to userId))
    }

    override suspend fun deleteEvent(eventId: String) {
        client.postgrest[tableName].delete {
            filter { eq("id", eventId) }
        }
    }
}
