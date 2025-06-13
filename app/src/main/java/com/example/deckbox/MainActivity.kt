package com.example.deckbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.deckbox.ui.navigation.NavGraph
import com.example.deckbox.viewmodel.AuthViewModel
import com.example.deckbox.viewmodel.DeckViewModel
import com.example.deckbox.viewmodel.ScryfallViewModel
import com.example.deckbox.ui.theme.Typography

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authViewModel: AuthViewModel by viewModels()
        val deckViewModel: DeckViewModel by viewModels()
        val scryfallViewModel: ScryfallViewModel by viewModels()

        setContent {
            MaterialTheme(
                typography = Typography
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        authViewModel = authViewModel,
                        deckViewModel = deckViewModel,
                        scryfallViewModel = scryfallViewModel
                    )
                }
            }
        }
    }
}