package com.example.deckbox.ui.decks.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

//No se ha llegado a integrar
@Composable
fun WinRateBar(winRate: Float) {
    Column {
        Text(
            text = "Winrate: ${"%.1f".format(winRate)}%",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { winRate / 100 },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = when {
                winRate > 60 -> Color.Green
                winRate > 40 -> Color.Yellow
                else -> Color.Red
            },
        )
    }
}