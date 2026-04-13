package pls.dev.sushitracker.data

data class SessionRecord(
    val id: String,
    val date: String,           // ISO 8601 format
    val restaurant: String,
    val pieces: Map<String, Int>,
    val totalPieces: Int
)
