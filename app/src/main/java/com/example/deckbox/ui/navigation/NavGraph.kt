package com.example.deckbox.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.deckbox.ui.auth.LoginScreen
import com.example.deckbox.ui.auth.RegisterScreen
import com.example.deckbox.ui.decks.CreateDeckScreen
import com.example.deckbox.ui.decks.DeckDetailScreen
import com.example.deckbox.ui.decks.DeckListScreen
import com.example.deckbox.viewmodel.AuthViewModel
import com.example.deckbox.viewmodel.DeckViewModel
import com.example.deckbox.viewmodel.ScryfallViewModel

@Composable
fun NavGraph(
    authViewModel: AuthViewModel,
    deckViewModel: DeckViewModel,
    scryfallViewModel: ScryfallViewModel
) {
    val navController = rememberNavController()
    val startDestination = if (authViewModel.user.value != null) "deckList" else "login"

    NavHost(
        navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(navController, authViewModel)
        }

        composable("register") {
            RegisterScreen(navController, authViewModel)
        }

        composable("deckList") {
            DeckListScreen(navController, authViewModel, deckViewModel)
        }

        composable("createDeck") {
            CreateDeckScreen(navController, authViewModel, deckViewModel, scryfallViewModel)
        }

        composable(
            "deckDetail/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.IntType })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId") ?: 0
            DeckDetailScreen(deckId, deckViewModel, authViewModel, navController)
        }
    }
}