package com.example.deckbox.ui.decks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.deckbox.data.model.ScryfallCard
import com.example.deckbox.viewmodel.ScryfallViewModel

@Composable
fun CardSearchResult(
    card: ScryfallCard,
    isSelected: Boolean,
    onClick: () -> Unit,
    scryfallViewModel: ScryfallViewModel
) {
    val imageUri = scryfallViewModel.getCardImage(card)
    val isDoubleFaced = scryfallViewModel.isDoubleFaced(card)

    if (imageUri == null) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(140.dp)
                .background(Color.LightGray)
                .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
        ) {
            Text(
                text = card.name,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Center)
            )
        }
        return
    }

    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val borderWidth = if (isSelected) 3.dp else 0.dp

    var isLoadingImage by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = card.name,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .width(100.dp)
                .height(140.dp),
            onState = { state ->
                isLoadingImage = state is AsyncImagePainter.State.Loading
            }
        )

        if (isLoadingImage) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (isDoubleFaced) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                    .size(20.dp)
            ) {
                Text(
                    text = "2",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}