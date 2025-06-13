package com.example.deckbox.ui.decks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.deckbox.R
import com.example.deckbox.ui.decks.components.DeckItem
import com.example.deckbox.viewmodel.AuthViewModel
import com.example.deckbox.viewmodel.DeckViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    deckViewModel: DeckViewModel
) {
    val user = authViewModel.user.value
    val decks = deckViewModel.decks.value
    val isLoading = deckViewModel.isLoading.value

    val hasInitialAnimationShown by deckViewModel.hasInitialAnimationShown.collectAsState()

    var showInitialLoading by remember { mutableStateOf(!hasInitialAnimationShown) }

    LaunchedEffect(showInitialLoading) {
        if (showInitialLoading) {
            delay(2500L)
            showInitialLoading = false
            deckViewModel.setInitialAnimationShown(true)
        }
    }

    LaunchedEffect(user) {
        user?.let {
            deckViewModel.loadDecks(it.nick)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(user?.name ?: "") },
                    actions = {
                        IconButton(onClick = {
                            authViewModel.logout()
                            deckViewModel.setInitialAnimationShown(false)
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("createDeck") }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Deck")
                }
            }
        ) { innerPadding ->
            if (isLoading && !showInitialLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (decks.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Aún no tienes ningún mazo!",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Pulsa el botón '+' para crear tu primer mazo.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(decks) { deck ->
                        DeckItem(
                            deck = deck,
                            onDeckClick = {
                                navController.navigate("deckDetail/${deck.id}")
                            }
                        )
                    }
                }
            }
        }
        LoadingAnimationOverlay(visible = showInitialLoading)
    }
}

@Composable
fun LoadingAnimationOverlay(
    visible: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            val rotation = remember { Animatable(0f) }

            LaunchedEffect(Unit) {
                rotation.animateTo(
                    targetValue = 720f,
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = LinearOutSlowInEasing
                    )
                )
            }

            Image(
                painter = painterResource(R.drawable.icon),
                contentDescription = "Cargando...",
                modifier = Modifier
                    .size(96.dp)
                    .rotate(rotation.value)
                    .clip(CircleShape)
            )
        }
    }
}

