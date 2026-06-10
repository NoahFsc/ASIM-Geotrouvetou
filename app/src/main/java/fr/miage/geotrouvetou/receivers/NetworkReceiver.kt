package fr.miage.geotrouvetou.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Notifie la perte et le retour de la connexion internet.
 *
 * À enregistrer dynamiquement (CONNECTIVITY_ACTION n'est plus délivré
 * aux receivers déclarés dans le manifest).
 */
class NetworkReceiver(
    private val onConnectivityChanged: (isConnected: Boolean) -> Unit,
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        @Suppress("DEPRECATION")
        if (intent.action != ConnectivityManager.CONNECTIVITY_ACTION) return
        onConnectivityChanged(isConnected(context))
    }

    companion object {
        @Suppress("DEPRECATION")
        fun intentFilter() = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

        fun isConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }
    }
}
