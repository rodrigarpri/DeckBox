package com.example.deckbox.data.model

import com.google.gson.annotations.SerializedName

data class ScryfallCard(
    val id: String,
    val name: String,
    @SerializedName("image_uris")
    val imageUris: ImageUris?,
    @SerializedName("legalities")
    val legalities: Map<String, String>?,
    @SerializedName("card_faces")
    val cardFaces: List<CardFace>?,
    @SerializedName("layout")
    val layout: String
)

data class CardFace(
    @SerializedName("image_uris")
    val imageUris: ImageUris?,
    val name: String
)

data class ImageUris(
    @SerializedName("normal")
    val normal: String
)

data class ScryfallSearchResponse(
    val data: List<ScryfallCard>
)