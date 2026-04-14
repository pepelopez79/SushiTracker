package pls.dev.sushitracker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class AchievementManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sushi_achievements", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val unlockedKey = "unlocked_achievements"

    private val sessionStorage = SessionStorage(context)

    fun getUnlockedIds(): Set<String> {
        val json = prefs.getString(unlockedKey, null) ?: return emptySet()
        val type = object : TypeToken<Set<String>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveUnlockedIds(ids: Set<String>) {
        prefs.edit { putString(unlockedKey, gson.toJson(ids)) }
    }

    fun unlockAchievement(id: String) {
        val current = getUnlockedIds().toMutableSet()
        current.add(id)
        saveUnlockedIds(current)
    }

    fun isUnlocked(id: String): Boolean {
        return getUnlockedIds().contains(id)
    }

    fun getProgress(achievement: Achievement): AchievementProgress {
        val sessions = sessionStorage.getSessions()

        return when (val req = achievement.requirement) {
            is AchievementRequirement.TotalPieces -> {
                val total = sessions.sumOf { it.totalPieces }
                AchievementProgress(total, req.count, total >= req.count)
            }

            is AchievementRequirement.SessionPieces -> {
                val maxSession = sessions.maxOfOrNull { it.totalPieces } ?: 0
                AchievementProgress(maxSession, req.count, maxSession >= req.count)
            }

            is AchievementRequirement.SpecificPieceTotal -> {
                val total = sessions.sumOf { it.pieces[req.pieceId] ?: 0 }
                AchievementProgress(total, req.count, total >= req.count)
            }

            is AchievementRequirement.SpecificPieceSession -> {
                val max = sessions.maxOfOrNull { it.pieces[req.pieceId] ?: 0 } ?: 0
                AchievementProgress(max, req.count, max >= req.count)
            }

            is AchievementRequirement.SessionsCompleted -> {
                val count = sessions.size
                AchievementProgress(count, req.count, count >= req.count)
            }

            is AchievementRequirement.PieceVariety -> {
                val allPieceIds = sessions.flatMap { it.pieces.keys }
                    .filter { pieceId -> sessions.any { (it.pieces[pieceId] ?: 0) > 0 } }
                    .toSet()
                AchievementProgress(allPieceIds.size, req.count, allPieceIds.size >= req.count)
            }

            is AchievementRequirement.AllPiecesInSession -> {
                val hasAllInOne = sessions.any { session ->
                    SUSHI_PIECES.all { piece ->
                        (session.pieces[piece.id] ?: 0) >= req.minCount
                    }
                }
                val distinctTypesInBestSession = sessions.maxOfOrNull { session ->
                    session.pieces.count { (_, count) -> count >= req.minCount }
                } ?: 0
                AchievementProgress(
                    distinctTypesInBestSession,
                    SUSHI_PIECES.size,
                    hasAllInOne
                )
            }
        }
    }

    fun checkAndUnlockAll(): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()
        val currentUnlocked = getUnlockedIds().toMutableSet()

        for (achievement in ACHIEVEMENTS) {
            if (!currentUnlocked.contains(achievement.id)) {
                val progress = getProgress(achievement)
                if (progress.isComplete) {
                    currentUnlocked.add(achievement.id)
                    newlyUnlocked.add(achievement)
                }
            }
        }

        if (newlyUnlocked.isNotEmpty()) {
            saveUnlockedIds(currentUnlocked)
        }

        return newlyUnlocked
    }

    fun getAllAchievementsWithStatus(): List<AchievementWithStatus> {
        val unlockedIds = getUnlockedIds()
        return ACHIEVEMENTS.map { achievement ->
            AchievementWithStatus(
                achievement = achievement,
                isUnlocked = unlockedIds.contains(achievement.id),
                progress = getProgress(achievement)
            )
        }
    }
}

data class AchievementProgress(
    val current: Int,
    val target: Int,
    val isComplete: Boolean
) {
    val percentage: Float get() = (current.toFloat() / target).coerceIn(0f, 1f)
}

data class AchievementWithStatus(
    val achievement: Achievement,
    val isUnlocked: Boolean,
    val progress: AchievementProgress
)
