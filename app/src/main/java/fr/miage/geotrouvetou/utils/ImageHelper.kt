package fr.miage.geotrouvetou.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import java.io.ByteArrayOutputStream
import kotlin.time.Duration.Companion.hours

class ImageHelper(private val client: SupabaseClient) {
    private val bucketName = "EventImages"
    private val bucketNameAvatar = "avatars"

    /**
     * Télécharge une image dans le bucket Supabase et retourne son URL publique.
     * Convertit l'image en WebP pour optimiser l'espace de stockage.
     * @param fileName Nom du fichier sans extension
     * @param bytes Contenu original de l'image (JPG/PNG)
     * @return L'URL publique de l'image stockée en .webp
     */
    suspend fun uploadAvatarImage(userId: String, bytes: ByteArray): String {
        val path = "avatar_$userId.webp"
        val payload = try { convertToWebp(bytes) } catch (_: Exception) { bytes }
        client.storage.from(bucketNameAvatar).upload(path, payload) { upsert = true }
        return path
    }

    suspend fun getAvatarSignedUrl(path: String): String {
        return client.storage.from(bucketNameAvatar).createSignedUrl(path, 1.hours)
    }

    suspend fun uploadEventImage(fileName: String, bytes: ByteArray): String {
        val finalFileName = if (fileName.endsWith(".webp")) fileName else "$fileName.webp"
        val payload = try {
            convertToWebp(bytes)
        } catch (e: Exception) {
            Log.e("ImageHelper", "Conversion WebP échouée, upload de l'image originale.", e)
            bytes
        }

        val bucket = client.storage.from(bucketName)
        bucket.upload(finalFileName, payload)

        return bucket.publicUrl(finalFileName)
    }

    /**
     * Compresse les bytes d'une image au format WebP (qualité 80%).
     */
    private fun convertToWebp(bytes: ByteArray): ByteArray {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: throw IllegalArgumentException("Impossible de décoder l'image en bitmap.")

        return ByteArrayOutputStream().use { out ->
            // Utilisation du format WEBP_LOSSY (disponible depuis Android 10/API 29+)
            // Ou WEBP pour les versions plus anciennes.
            // Ici on utilise WEBP_LOSSY à 80% de qualité pour un excellent ratio poids/qualité.
            val success = bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, out)
            if (!success) {
                throw IllegalStateException("Échec de la compression WebP.")
            }
            out.toByteArray()
        }
    }
}
