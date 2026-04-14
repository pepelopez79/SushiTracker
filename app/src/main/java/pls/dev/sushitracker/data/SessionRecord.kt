package pls.dev.sushitracker.data

data class SessionRecord(
    val id: String,
    val date: String,
    val restaurant: String,
    val pieces: Map<String, Int>,
    val totalPieces: Int
)
