package pls.dev.sushitracker.data

import pls.dev.sushitracker.R

data class SushiPiece(
    val id: String,
    val name: String,
    val imageRes: Int
)

val SUSHI_PIECES = listOf(
    SushiPiece("nigiri", "Nigiri", R.drawable.nigiri),
    SushiPiece("sashimi", "Sashimi", R.drawable.sashimi),
    SushiPiece("maki", "Maki", R.drawable.maki),
    SushiPiece("uramaki", "Uramaki", R.drawable.uramaki),
    SushiPiece("gunkan", "Gunkan", R.drawable.gunkan),
    SushiPiece("temaki", "Temaki", R.drawable.temaki),
    SushiPiece("gyoza", "Gyoza", R.drawable.gyoza),
    SushiPiece("tempura", "Tempura", R.drawable.tempura),
    SushiPiece("california", "California Roll", R.drawable.california),
    SushiPiece("dragon", "Dragon Roll", R.drawable.dragon),
    SushiPiece("edamame", "Edamame", R.drawable.edamame),
    SushiPiece("takoyaki", "Takoyaki", R.drawable.takoyaki),
)
