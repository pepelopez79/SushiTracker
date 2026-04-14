package pls.dev.sushitracker.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import pls.dev.sushitracker.data.SUSHI_PIECES
import pls.dev.sushitracker.data.SessionRecord
import pls.dev.sushitracker.data.StatsFilter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.core.graphics.toColorInt

object ExportUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun exportStatsToPdf(
        context: Context,
        pieceStats: Map<String, Int>,
        total: Int,
        filter: StatsFilter,
        sessions: List<SessionRecord>
    ): Uri? {
        val document = PdfDocument()

        val pageWidth = 595
        val pageHeight = 842

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = "#1B2838".toColorInt()
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = "#4ECDC4".toColorInt()
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = "#333333".toColorInt()
            textSize = 12f
            isAntiAlias = true
        }

        val headerPaint = Paint().apply {
            color = "#1B2838".toColorInt()
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = "#E0E0E0".toColorInt()
            strokeWidth = 1f
        }

        var yOffset = 50f
        val marginLeft = 40f
        val marginRight = pageWidth - 40f

        canvas.drawText("SUSHI TRACKER", marginLeft, yOffset, titlePaint)
        yOffset += 30f

        val filterLabel = when (filter) {
            StatsFilter.ALL -> "Estadisticas - Todos los tiempos"
            StatsFilter.YEAR -> "Estadisticas - Este ano"
            StatsFilter.MONTH -> "Estadisticas - Este mes"
            StatsFilter.WEEK -> "Estadisticas - Esta semana"
        }
        canvas.drawText(filterLabel, marginLeft, yOffset, subtitlePaint)
        yOffset += 15f

        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        canvas.drawText("Generado el: $now", marginLeft, yOffset, bodyPaint)
        yOffset += 30f

        canvas.drawLine(marginLeft, yOffset, marginRight, yOffset, linePaint)
        yOffset += 20f

        canvas.drawText("RESUMEN", marginLeft, yOffset, subtitlePaint)
        yOffset += 20f

        canvas.drawText("Total de piezas: $total", marginLeft, yOffset, bodyPaint)
        yOffset += 18f

        canvas.drawText("Sesiones registradas: ${sessions.size}", marginLeft, yOffset, bodyPaint)
        yOffset += 18f

        val avgPerSession = if (sessions.isNotEmpty()) total / sessions.size else 0
        canvas.drawText("Promedio por sesion: $avgPerSession piezas", marginLeft, yOffset, bodyPaint)
        yOffset += 30f

        canvas.drawLine(marginLeft, yOffset, marginRight, yOffset, linePaint)
        yOffset += 20f

        canvas.drawText("DESGLOSE POR TIPO", marginLeft, yOffset, subtitlePaint)
        yOffset += 25f

        canvas.drawText("Tipo", marginLeft, yOffset, headerPaint)
        canvas.drawText("Cantidad", marginLeft + 200f, yOffset, headerPaint)
        canvas.drawText("Porcentaje", marginLeft + 320f, yOffset, headerPaint)
        yOffset += 5f
        canvas.drawLine(marginLeft, yOffset, marginRight, yOffset, linePaint)
        yOffset += 15f

        val sortedPieces = pieceStats
            .filter { it.value > 0 }
            .toList()
            .sortedByDescending { it.second }

        sortedPieces.forEach { (pieceId, count) ->
            val pieceName = SUSHI_PIECES.find { it.id == pieceId }?.name ?: pieceId
            val percentage = if (total > 0) (count * 100f / total) else 0f

            canvas.drawText(pieceName, marginLeft, yOffset, bodyPaint)
            canvas.drawText("$count", marginLeft + 200f, yOffset, bodyPaint)
            canvas.drawText("%.1f%%".format(percentage), marginLeft + 320f, yOffset, bodyPaint)
            yOffset += 18f

            if (yOffset > pageHeight - 100) {
                canvas.drawText("...", marginLeft, yOffset, bodyPaint)
                return@forEach
            }
        }

        yOffset += 20f
        canvas.drawLine(marginLeft, yOffset, marginRight, yOffset, linePaint)
        yOffset += 20f

        canvas.drawText("TOTAL", marginLeft, yOffset, headerPaint)
        canvas.drawText("$total", marginLeft + 200f, yOffset, headerPaint)
        canvas.drawText("100%", marginLeft + 320f, yOffset, headerPaint)

        document.finishPage(page)

        return try {
            val file = File(context.cacheDir, "sushi_stats_${System.currentTimeMillis()}.pdf")
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun exportStatsToExcel(
        context: Context,
        pieceStats: Map<String, Int>,
        total: Int,
        filter: StatsFilter,
        sessions: List<SessionRecord>
    ): Uri? {
        return try {
            val file = File(context.cacheDir, "sushi_stats_${System.currentTimeMillis()}.csv")
            FileWriter(file).use { writer ->
                writer.write("\uFEFF")

                writer.write("SUSHI TRACKER - Estadisticas\n")
                writer.write("Filtro: ${getFilterLabel(filter)}\n")
                writer.write("Fecha: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}\n")
                writer.write("\n")

                writer.write("RESUMEN\n")
                writer.write("Total de piezas,$total\n")
                writer.write("Sesiones registradas,${sessions.size}\n")
                val avg = if (sessions.isNotEmpty()) total / sessions.size else 0
                writer.write("Promedio por sesion,$avg\n")
                writer.write("\n")

                writer.write("DESGLOSE POR TIPO\n")
                writer.write("Tipo,Cantidad,Porcentaje\n")

                val sortedPieces = pieceStats
                    .filter { it.value > 0 }
                    .toList()
                    .sortedByDescending { it.second }

                sortedPieces.forEach { (pieceId, count) ->
                    val pieceName = SUSHI_PIECES.find { it.id == pieceId }?.name ?: pieceId
                    val percentage = if (total > 0) (count * 100f / total) else 0f
                    writer.write("$pieceName,$count,%.1f%%\n".format(percentage))
                }

                writer.write("TOTAL,$total,100%\n")
                writer.write("\n")

                writer.write("HISTORIAL DE SESIONES\n")
                writer.write("Fecha,Restaurante,Total Piezas\n")

                sessions.forEach { session ->
                    val date = try {
                        LocalDateTime.parse(session.date, DateTimeFormatter.ISO_DATE_TIME)
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    } catch (e: Exception) {
                        session.date
                    }
                    writer.write("$date,${session.restaurant},${session.totalPieces}\n")
                }
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

    fun openFile(context: Context, uri: Uri, mimeType: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Abrir con..."))
    }

    fun shareFile(context: Context, uri: Uri, mimeType: String, title: String = "Compartir archivo") {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    private fun getFilterLabel(filter: StatsFilter): String {
        return when (filter) {
            StatsFilter.ALL -> "Todos los tiempos"
            StatsFilter.YEAR -> "Este ano"
            StatsFilter.MONTH -> "Este mes"
            StatsFilter.WEEK -> "Esta semana"
        }
    }
}
