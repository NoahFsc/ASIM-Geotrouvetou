package fr.miage.geotrouvetou.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import fr.miage.geotrouvetou.App
import fr.miage.geotrouvetou.R
import fr.miage.geotrouvetou.ui.components.atoms.Toast
import fr.miage.geotrouvetou.ui.auth.LoginScreen
import fr.miage.geotrouvetou.ui.auth.RegisterScreen
import fr.miage.geotrouvetou.ui.components.molecules.NavBar
import fr.miage.geotrouvetou.ui.components.molecules.NavTab
import fr.miage.geotrouvetou.ui.events.CreateEventScreen
import fr.miage.geotrouvetou.ui.map.MapScreen
import fr.miage.geotrouvetou.ui.profile.ProfileScreen
import io.github.jan.supabase.auth.auth

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAP = "map"
    const val PROFILE = "profile"
    const val CREATE_EVENT = "createEvent"
}

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val app = context.applicationContext as? App

    var selectedTab by remember { mutableStateOf(NavTab.Carte) }
    var loginToastKey by remember { mutableIntStateOf(0) }
    var eventCreatedToastKey by remember { mutableIntStateOf(0) }

    fun navigateIfLoggedIn(destination: String, tab: NavTab) {
        selectedTab = tab
        val isLoggedIn = runCatching {
            app?.supabase?.auth?.currentSessionOrNull() != null
        }.getOrDefault(false)

        if (isLoggedIn) {
            navController.navigate(destination)
        } else {
            navController.navigate(Routes.LOGIN)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.MAP,
            modifier = Modifier.weight(1f),
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        val dest = when (selectedTab) {
                            NavTab.Profil -> Routes.PROFILE
                            NavTab.Ajouter -> Routes.CREATE_EVENT
                            NavTab.Carte -> Routes.MAP
                        }
                        navController.navigate(dest) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                        loginToastKey++
                    },
                    onRegisterClick = { navController.navigate(Routes.REGISTER) },
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.MAP) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() },
                )
            }

            composable(Routes.MAP) {
                MapScreen()
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLogout = {
                        selectedTab = NavTab.Carte
                        navController.navigate(Routes.MAP) {
                            popUpTo(Routes.MAP) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() },
                )
            }

            composable(Routes.CREATE_EVENT) {
                CreateEventScreen(
                    onEventCreated = {
                        selectedTab = NavTab.Carte
                        navController.navigate(Routes.MAP) {
                            popUpTo(Routes.CREATE_EVENT) { inclusive = true }
                        }
                        eventCreatedToastKey++
                    },
                )
            }
        }

        if (loginToastKey > 0) {
            Toast(
                title = "Connexion réussie !",
                description = "Bienvenue sur Geo Trouvetou",
                key = loginToastKey,
            )
        }

        if (eventCreatedToastKey > 0) {
            Toast(
                title = "Succès !",
                description = "L'événement a été créé avec succès",
                key = eventCreatedToastKey,
            )
        }

        NavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    when (tab) {
                        NavTab.Carte -> {
                            selectedTab = NavTab.Carte
                            navController.navigate(Routes.MAP) {
                                popUpTo(Routes.MAP) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                        NavTab.Profil -> navigateIfLoggedIn(Routes.PROFILE, NavTab.Profil)
                        NavTab.Ajouter -> navigateIfLoggedIn(Routes.CREATE_EVENT, NavTab.Ajouter)
                    }
                },
                modifier = Modifier
                    .background(colorResource(R.color.white))
                    .navigationBarsPadding(),
            )
    }
}
