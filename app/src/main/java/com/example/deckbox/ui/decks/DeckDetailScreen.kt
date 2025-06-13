package com.example.deckbox.ui.decks

import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.deckbox.R
import com.example.deckbox.viewmodel.AuthViewModel
import com.example.deckbox.viewmodel.DeckViewModel

@Composable
fun DeckDetailScreen(
    deckId: Int,
    deckViewModel: DeckViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val user = authViewModel.user.value
    val deck by deckViewModel.currentDeck.collectAsState()
    Log.d(
        "DeckDetailScreen",
        "Deck UI recompuesta. Deck actual: ${deck?.name}, Wins: ${deck?.wins}"
    )
    val context = LocalContext.current

    var localDeck by remember { mutableStateOf(deck) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var isUpdatingDeck by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    val sonidoWin = MediaPlayer()
    val sonidoLose = MediaPlayer()
    val sonidoTrash = MediaPlayer()
    sonidoWin.setVolume(0.5f, 0.5f)
    sonidoLose.setVolume(0.5f, 0.5f)
    sonidoTrash.setVolume(0.5f, 0.5f)

    sonidoWin.setDataSource(context.resources.openRawResourceFd(R.raw.win))
    sonidoLose.setDataSource(context.resources.openRawResourceFd(R.raw.lose))
    sonidoTrash.setDataSource(context.resources.openRawResourceFd(R.raw.trash))

    sonidoTrash.prepare()
    sonidoWin.prepare()
    sonidoLose.prepare()

    LaunchedEffect(deck) {
        localDeck = deck
    }

    LaunchedEffect(deckId, user?.nick) {
        if (user?.nick != null) {
            deckViewModel.loadDeckById(deckId, user.nick)
        }
    }

    if (deck == null) {
        Text("Cargando mazo o mazo no encontrado...", modifier = Modifier.padding(16.dp))
        LaunchedEffect(deck) {
            if (deck == null) {
                navController.popBackStack()
            }
        }
        return
    }

    Scaffold { innerPadding ->
        if (deck == null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando mazo o mazo no encontrado...")
            }
            return@Scaffold
        }

        localDeck?.let { actualDeck ->

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.height(20.dp))

                AsyncImage(
                    model = actualDeck.image,
                    contentDescription = actualDeck.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showImageDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(actualDeck.name, style = MaterialTheme.typography.headlineMedium)
                Text(
                    actualDeck.format,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Victorias", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            actualDeck.wins.toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Winrate", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${"%.1f".format(actualDeck.winRate)}%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = when {
                                actualDeck.winRate > 60 -> Color.Green
                                actualDeck.winRate > 40 -> Color.Yellow
                                else -> Color.Red
                            }
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Derrotas", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            actualDeck.losses.toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de victoria/derrota y papelera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AnimatedClickButton(
                        onClick = {
                            if (user != null) {
                                localDeck = actualDeck.copy(wins = actualDeck.wins + 1)

                                deckViewModel.updateDeck(
                                    actualDeck.id,
                                    actualDeck.copy(wins = actualDeck.wins + 1)

                                )
                                Toast.makeText(context, "Victoria registrada", Toast.LENGTH_SHORT)
                                    .show()
                                if (sonidoWin.isPlaying) {
                                    sonidoWin.seekTo(0)
                                } else {
                                    sonidoWin.start()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Icon(Icons.Default.ThumbUp, contentDescription = "add win")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Victoria")
                    }

                    AnimatedClickButton(
                        onClick = {
                            showDeleteDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Deck")
                    }

                    AnimatedClickButton(
                        onClick = {
                            if (user != null) {
                                localDeck = actualDeck.copy(losses = actualDeck.losses + 1)

                                deckViewModel.updateDeck(
                                    actualDeck.id,
                                    actualDeck.copy(losses = actualDeck.losses + 1)
                                )
                                Toast.makeText(context, "Derrota añadida", Toast.LENGTH_SHORT)
                                    .show()
                                if (sonidoLose.isPlaying) {
                                    sonidoLose.seekTo(0)
                                } else {
                                    sonidoLose.start()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Icon(Icons.Default.ThumbDown, contentDescription = "add lose")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Derrota")
                    }
                }
                if (showDeleteDialog) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        confirmButton = {
                            Button(onClick = {
                                if (user != null) {
                                    deckViewModel.deleteDeck(actualDeck.id)
                                    Toast.makeText(context, "Mazo eliminado", Toast.LENGTH_SHORT)
                                        .show()
                                    if (sonidoTrash.isPlaying) {
                                        sonidoTrash.seekTo(0)
                                    } else {
                                        sonidoTrash.start()
                                    }
                                    showDeleteDialog = false
                                }
                            }) {
                                Text("Sí, eliminar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDeleteDialog = false }) {
                                Text("Cancelar")
                            }
                        },
                        title = { Text("Confirmar eliminación") },
                        text = { Text("¿Seguro que quieres eliminar el mazo? Esta acción no se puede deshacer.") }
                    )
                }

            }
            if (showImageDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 1f))
                        .clickable { showImageDialog = false },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = actualDeck.image,
                        contentDescription = actualDeck.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clickable { showImageDialog = false },
                    )
                }
            }


        }
    }

}

@Composable
fun AnimatedClickButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "button-scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .scale(scale),
        colors = colors,
        interactionSource = interactionSource
    ) {
        content()
    }
}
