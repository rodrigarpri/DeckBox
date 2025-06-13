package com.example.deckbox.ui.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.deckbox.data.model.DeckCreateRequest
import com.example.deckbox.ui.decks.components.CardSearchResult
import com.example.deckbox.viewmodel.AuthViewModel
import com.example.deckbox.viewmodel.DeckViewModel
import com.example.deckbox.viewmodel.ScryfallViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeckScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    deckViewModel: DeckViewModel,
    scryfallViewModel: ScryfallViewModel

) {
    var name by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val viewModelScope = rememberCoroutineScope()
    val searchResults = scryfallViewModel.searchResults.value
    val user = authViewModel.user.value

    val formats by scryfallViewModel.formats

    var expanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scryfallViewModel.resetSearch()
    }

    DisposableEffect(Unit) {
        onDispose {
            scryfallViewModel.resetSearch()
        }
    }

    LaunchedEffect(deckViewModel.decks.value) {
        val deckExists = deckViewModel.decks.value.any {
            it.name == name && it.user == user?.nick
        }

        if (deckExists) {
            navController.popBackStack()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.height(46.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del mazo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                value = format,
                onValueChange = {},
                readOnly = true,
                label = { Text("Formato") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                formats.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            format = item
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Buscador de cartas para imagen
        Text("Selector de imagen/comandante", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                scryfallViewModel.searchCards(it)
            },
            label = { Text("Busca una carta por nombre") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchResults.size) { index ->
                val card = searchResults[index]
                CardSearchResult(
                    card = card,
                    isSelected = selectedImage == scryfallViewModel.getCardImage(card),
                    onClick = {
                        scryfallViewModel.getCardImage(card)?.let { uri ->
                            selectedImage = uri
                        }
                    },
                    scryfallViewModel = scryfallViewModel
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (user != null && name.isNotEmpty() && format.isNotEmpty() && selectedImage.isNotEmpty()) {
                    val newDeck = DeckCreateRequest(
                        name = name,
                        image = selectedImage,
                        format = format,
                        wins = 0,
                        losses = 0,
                        user = user.nick
                    )

                    isLoading = true
                    errorMessage = null

                    viewModelScope.launch {
                        try {
                            deckViewModel.createDeck(newDeck)
                        } catch (e: Exception) {
                            errorMessage = if (e.message?.contains("El deck ya existe") == true) {
                                "Ya tienes un mazo con ese nombre"
                            } else {
                                "Error al crear el mazo: ${e.message}"
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && name.isNotEmpty() && format.isNotEmpty() && selectedImage.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Crear mazo")
            }
        }

        errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}