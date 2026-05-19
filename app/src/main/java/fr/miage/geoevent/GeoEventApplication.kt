package fr.miage.geoevent

import android.app.Application
import fr.miage.geoevent.data.backend.SupabaseDatabaseService
import fr.miage.geoevent.domain.interfaces.IDatabaseService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

class GeoEventApplication : Application() {

    val supabase: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }

    val databaseService: IDatabaseService by lazy {
        SupabaseDatabaseService(supabase)
    }
}
