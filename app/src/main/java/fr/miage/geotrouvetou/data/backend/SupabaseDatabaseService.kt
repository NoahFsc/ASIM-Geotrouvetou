package fr.miage.geotrouvetou.data.backend

import fr.miage.geotrouvetou.domain.interfaces.IDatabaseService
import fr.miage.geotrouvetou.domain.models.Evenement
import fr.miage.geotrouvetou.domain.models.User
import fr.miage.geotrouvetou.utils.ImageHelper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
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
        val user = client.postgrest["profiles"].select {
            filter { eq("id", userId) }
        }.decodeSingleOrNull<User>() ?: return null

        val resolvedAvatarUrl = user.avatarUrl?.let { path ->
            if (path.startsWith("http")) path
            else try { imageHelper.getAvatarSignedUrl(path) } catch (_: Exception) { null }
        }
        return user.copy(avatarUrl = resolvedAvatarUrl)
    }

    override suspend fun createProfile(user: User) {
        client.postgrest["profiles"].insert(user)
    }

    override suspend fun updateProfile(userId: String, fullName: String) {
        client.postgrest["profiles"].update({
            set("full_name", fullName)
        }) {
            filter { eq("id", userId) }
        }
        client.auth.updateUser {
            data = buildJsonObject { put("full_name", fullName) }
        }
    }

    override suspend fun updateAvatar(userId: String, bytes: ByteArray) {
        val avatarUrl = imageHelper.uploadAvatarImage(userId, bytes)
        client.postgrest["profiles"].update({
            set("avatar_url", avatarUrl)
        }) {
            filter { eq("id", userId) }
        }
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
}
