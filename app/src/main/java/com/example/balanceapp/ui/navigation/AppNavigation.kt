package com.example.balanceapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.example.balanceapp.ui.screens.PokemonListScreen
import com.example.balanceapp.ui.screens.LoginScreen
import com.example.balanceapp.ui.screens.RegistroScreen
import com.example.balanceapp.ui.screens.WelcomeScreen
import com.example.balanceapp.ui.screens.TeamSelectionScreen
import com.example.balanceapp.ui.screens.ProfileScreen

// Objeto donde guardamos las rutas de cada pantalla.
object Routes {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val WELCOME = "welcome"
    const val POKEMON_LIST = "pokemon_list"
    const val TEAM_SELECTION = "team_selection"
    const val PROFILE = "profile"
}

// Esta función arma toda la navegación de la app.
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    // Controlador de navegación.
    val navController = rememberNavController()

    // Si el usuario ya está autenticado en Firebase, partimos desde WELCOME
    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) {
        Routes.WELCOME
    } else {
        Routes.LOGIN
    }

    // NavHost: aquí definimos la pantalla inicial y cada ruta de la app.
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // pantalla login
        composable(Routes.LOGIN) {
            // Llamamos al composable LoginScreen
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(0) // limpia el historial para que no se pueda volver atrás al login.
                    }
                },
                onIrARegistro = {
                    // Si el usuario no tiene cuenta, lo mandamos a Registro.
                    navController.navigate(Routes.REGISTRO)
                }
            )
        }
        // pantalla de registro
        composable(Routes.REGISTRO) {

            RegistroScreen(
                // Si el registro es correcto, lo mandamos a elegir team
                onRegistroExitoso = {
                    navController.navigate(Routes.TEAM_SELECTION) {
                        popUpTo(0)
                    }
                },
                onVolverALogin = {
                    // Vuelve a la pantalla anterior
                    navController.navigateUp()
                }
            )
        }// pantalla de bienvenida
        composable(Routes.WELCOME) {
            //pantalla de bienvenida, solo tiene un botón para continuar.
            WelcomeScreen(onGetStartedClick = {
                // Cuando aprieta "Get Started", manda a la lista de Pokémon.
                navController.navigate(Routes.POKEMON_LIST) })
        }
        composable(Routes.TEAM_SELECTION) {
            TeamSelectionScreen(
                onTeamSelectedAndSaved = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(Routes.POKEMON_LIST) {
            PokemonListScreen(
                onBack = {
                // Si queremos volver atrás desde la lista
                    navController.navigateUp()
                },
                onLogout = {
                    // Cerramos sesión en Firebase y volvemos al login limpiando el historial
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                },
                onOpenProfile = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.navigateUp() }
            )
        }
    }
}


