package fr.miage.geotrouvetou

import android.app.Application
import fr.miage.geotrouvetou.data.backend.SupabaseDatabaseService
import fr.miage.geotrouvetou.domain.interfaces.IDatabaseService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.okhttp.OkHttp

class App : Application() {

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

    val databaseService: IDatabaseService by lazy {
        SupabaseDatabaseService(supabase)
    }
}
