package pls.dev.sushitracker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

enum class AppTheme(val id: String, mapOf: Map<AppLanguage, String>) {
    DARK(
        "dark",
        mapOf(
            AppLanguage.SPANISH to "Modo oscuro",
            AppLanguage.ENGLISH to "Dark mode",
            AppLanguage.FRENCH to "Mode sombre",
            AppLanguage.ITALIAN to "Modalita scura"
        )
    ),
    LIGHT(
        "light",
        mapOf(
            AppLanguage.SPANISH to "Modo claro",
            AppLanguage.ENGLISH to "Light mode",
            AppLanguage.FRENCH to "Mode clair",
            AppLanguage.ITALIAN to "Modalita chiara"
        )
    ),
    SALMON(
        "salmon",
        mapOf(
            AppLanguage.SPANISH to "Modo salmón",
            AppLanguage.ENGLISH to "Salmon mode",
            AppLanguage.FRENCH to "Mode saumon",
            AppLanguage.ITALIAN to "Modalita salmone"
        )
    )
}

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    SPANISH("es", "Espanol", "ES"),
    ENGLISH("en", "English", "EN"),
    FRENCH("fr", "Francais", "FR"),
    ITALIAN("it", "Italiano", "IT")
}

data class CustomPiece(
    val id: String,
    val name: String,
    val imageUri: String? = null
)

class AppSettingsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sushi_app_settings", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getTheme(): AppTheme {
        val themeId = prefs.getString("theme", AppTheme.DARK.id) ?: AppTheme.DARK.id
        return AppTheme.entries.find { it.id == themeId } ?: AppTheme.DARK
    }

    fun setTheme(theme: AppTheme) {
        prefs.edit { putString("theme", theme.id) }
    }

    fun getLanguage(): AppLanguage {
        val code = prefs.getString("language", AppLanguage.SPANISH.code) ?: AppLanguage.SPANISH.code
        return AppLanguage.entries.find { it.code == code } ?: AppLanguage.SPANISH
    }

    fun setLanguage(language: AppLanguage) {
        prefs.edit { putString("language", language.code) }
    }

    fun getCustomPieces(): List<CustomPiece> {
        val json = prefs.getString("custom_pieces", null) ?: return emptyList()
        val type = object : TypeToken<List<CustomPiece>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveCustomPieces(pieces: List<CustomPiece>) {
        prefs.edit { putString("custom_pieces", gson.toJson(pieces)) }
    }

    fun addCustomPiece(piece: CustomPiece) {
        val current = getCustomPieces().toMutableList()
        current.add(piece)
        saveCustomPieces(current)
    }

    fun removeCustomPiece(pieceId: String) {
        val current = getCustomPieces().toMutableList()
        current.removeAll { it.id == pieceId }
        saveCustomPieces(current)
    }

    fun updateCustomPiece(piece: CustomPiece) {
        val current = getCustomPieces().toMutableList()
        val index = current.indexOfFirst { it.id == piece.id }
        if (index != -1) {
            current[index] = piece
            saveCustomPieces(current)
        }
    }
}
