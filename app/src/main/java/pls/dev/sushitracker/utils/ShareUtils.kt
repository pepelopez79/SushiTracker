package pls.dev.sushitracker.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import pls.dev.sushitracker.data.SUSHI_PIECES
import pls.dev.sushitracker.data.SessionRecord
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.core.graphics.toColorInt
import androidx.core.graphics.createBitmap

object ShareUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateSessionShareText(session: SessionRecord): String {
        val date = try {
            LocalDateTime.parse(session.date, DateTimeFormatter.ISO_DATE_TIME)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        } catch (e: Exception) {
            session.date
        }

        val piecesDetail = session.pieces
            .filter { it.value > 0 }
            .map { (pieceId, count) ->
                val pieceName = SUSHI_PIECES.find { it.id == pieceId }?.name ?: pieceId
                "  - $pieceName: $count"
            }
            .joinToString("\n")

        return """
            |SUSHI TRACKER
            |
            |Sesion en: ${session.restaurant}
            |Fecha: $date
            |Total: ${session.totalPieces} piezas
            |
            |Detalle:
            |$piecesDetail
            |
            |#SushiTracker #Sushi
        """.trimMargin()
    }

    fun shareText(context: Context, text: String, title: String = "Compartir sesion") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateSessionImage(
        context: Context,
        session: SessionRecord
    ): Uri? {
        val width = 1080
        val height = 1920
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        val bgColor = "#1B2838".toColorInt()
        val primaryColor = "#4ECDC4".toColorInt()
        val textColor = Color.WHITE
        val mutedColor = "#94A3B3".toColorInt()

        canvas.drawColor(bgColor)

        val titlePaint = Paint().apply {
            color = primaryColor
            textSize = 72f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = textColor
            textSize = 48f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = mutedColor
            textSize = 36f
            isAntiAlias = true
        }

        val numberPaint = Paint().apply {
            color = primaryColor
            textSize = 200f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText("SUSHI TRACKER", 80f, 150f, titlePaint)

        canvas.drawText(session.restaurant, 80f, 250f, subtitlePaint)

        val date = try {
            LocalDateTime.parse(session.date, DateTimeFormatter.ISO_DATE_TIME)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            session.date
        }
        canvas.drawText(date, 80f, 320f, bodyPaint)

        canvas.drawText("${session.totalPieces}", width / 2f, 650f, numberPaint)

        val piecesTitlePaint = Paint().apply {
            color = textColor
            textSize = 48f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("PIEZAS", width / 2f, 750f, piecesTitlePaint)

        val topPieces = session.pieces
            .filter { it.value > 0 }
            .toList()
            .sortedByDescending { it.second }
            .take(6)

        var yOffset = 900f
        val itemPaint = Paint().apply {
            color = textColor
            textSize = 40f
            isAntiAlias = true
        }
        val countPaint = Paint().apply {
            color = primaryColor
            textSize = 40f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }

        topPieces.forEach { (pieceId, count) ->
            val pieceName = SUSHI_PIECES.find { it.id == pieceId }?.name ?: pieceId
            canvas.drawText(pieceName, 80f, yOffset, itemPaint)
            canvas.drawText("$count", width - 80f, yOffset, countPaint)
            yOffset += 70f
        }

        val footerPaint = Paint().apply {
            color = mutedColor
            textSize = 32f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("#SushiTracker", width / 2f, height - 100f, footerPaint)

        return try {
            val file = File(context.cacheDir, "session_share_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareImage(context: Context, imageUri: Uri, text: String? = null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            text?.let { putExtra(Intent.EXTRA_TEXT, it) }
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir en..."))
    }
}
