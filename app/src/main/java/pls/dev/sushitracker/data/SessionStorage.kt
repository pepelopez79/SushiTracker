package pls.dev.sushitracker.data

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class SessionStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sushi_tracker_sessions", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "sessions"

    fun getSessions(): List<SessionRecord> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<SessionRecord>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveSession(session: SessionRecord) {
        val sessions = getSessions().toMutableList()
        sessions.add(0, session)
        prefs.edit().putString(key, gson.toJson(sessions)).apply()
    }

    fun getSessionById(id: String): SessionRecord? {
        return getSessions().find { it.id == id }
    }

    fun getRanking(limit: Int = 10): List<SessionRecord> {
        return getSessions()
            .sortedByDescending { it.totalPieces }
            .take(limit)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getStats(filter: StatsFilter): StatsResult {
        val sessions = getSessions()
        val now = LocalDate.now()

        val startDate: LocalDate = when (filter) {
            StatsFilter.ALL -> LocalDate.MIN
            StatsFilter.YEAR -> now.withDayOfYear(1)
            StatsFilter.MONTH -> now.withDayOfMonth(1)
            StatsFilter.WEEK -> now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        }

        val filtered = if (filter == StatsFilter.ALL) {
            sessions
        } else {
            sessions.filter { session ->
                try {
                    val sessionDate = LocalDateTime.parse(session.date, DateTimeFormatter.ISO_DATE_TIME)
                        .toLocalDate()
                    !sessionDate.isBefore(startDate)
                } catch (e: Exception) {
                    false
                }
            }
        }

        val pieceStats = mutableMapOf<String, Int>()
        var total = 0

        for (session in filtered) {
            for ((pieceId, count) in session.pieces) {
                if (count > 0) {
                    pieceStats[pieceId] = (pieceStats[pieceId] ?: 0) + count
                    total += count
                }
            }
        }

        return StatsResult(pieceStats, total)
    }
}

enum class StatsFilter {
    ALL, YEAR, MONTH, WEEK
}

data class StatsResult(
    val pieceStats: Map<String, Int>,
    val total: Int
)
