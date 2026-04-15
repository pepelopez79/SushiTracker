package pls.dev.sushitracker.data

import pls.dev.sushitracker.R

data class SushiPiece(
    val id: String,
    val name: String,
    val imageRes: Int,
    val emoji: String
)

val SUSHI_PIECES = listOf(
    SushiPiece("nigiri",    "Nigiri",         R.drawable.nigiri,    "🍣"),
    SushiPiece("sashimi",   "Sashimi",        R.drawable.sashimi,   "🐟"),
    SushiPiece("maki",      "Maki",           R.drawable.maki,      "🍥"),
    SushiPiece("onigiri",   "Onigiri",        R.drawable.onigiri,   "🍙"),
    SushiPiece("uramaki",   "Uramaki",        R.drawable.uramaki,   "🍘"),
    SushiPiece("gunkan",    "Gunkan",         R.drawable.gunkan,    "🫔"),
    SushiPiece("temaki",    "Temaki",         R.drawable.temaki,    "🌮"),
    SushiPiece("gyoza",     "Gyoza",          R.drawable.gyoza,     "🥟"),
    SushiPiece("tempura",   "Tempura",        R.drawable.tempura,   "🍤"),
    SushiPiece("edamame",   "Edamame",        R.drawable.edamame,   "🫛"),
    SushiPiece("takoyaki",  "Takoyaki",       R.drawable.takoyaki,  "🐙"),
)

fun getPieceEmoji(id: String, customPieces: List<CustomPiece> = emptyList()): String {
    SUSHI_PIECES.find { it.id == id }?.let { return it.emoji }
    customPieces.find { it.id == id }?.let { return it.emoji }
    return "🍣"
}

fun getPieceName(id: String, customPieces: List<CustomPiece> = emptyList()): String {
    SUSHI_PIECES.find { it.id == id }?.let { return it.name }
    customPieces.find { it.id == id }?.let { return it.name }
    return id.replaceFirstChar { it.uppercase() }
}
