package fr.miage.geoevent

import android.app.Application
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.okhttp.OkHttp

// Point d'entrée unique pour le client Supabase : toutes les Activity y accèdent via
// (applicationContext as GeoEventApplication).supabase, sans dépendance entre elles.
class GeoEventApplication : Application() {

    // "by lazy" garantit une seule instance créée à la première utilisation,
    // jamais avant que BuildConfig soit disponible.
    val supabase: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            httpEngine = OkHttp.create()

            install(Auth)      // Nécessaire pour gérer l'utilisateur connecté (ID, session)
            install(Postgrest) // Pour les opérations CRUD sur la base de données
            install(Realtime)  // Pour la mise à jour automatique de la carte
            install(Storage)   // Pour gérer l'upload des photos dans le bucket EventImages
        }
    }
}