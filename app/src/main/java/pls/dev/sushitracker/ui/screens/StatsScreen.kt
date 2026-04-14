package pls.dev.sushitracker.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pls.dev.sushitracker.data.AppStrings
import pls.dev.sushitracker.data.SessionStorage
import pls.dev.sushitracker.data.StatsFilter
import pls.dev.sushitracker.ui.theme.*
import pls.dev.sushitracker.utils.ExportUtils

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DefaultLocale")
@Composable
fun StatsScreen(
    colors: SushiColors,
    strings: AppStrings.Strings,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionStorage(context) }
    val scope = rememberCoroutineScope()
    var stats by remember { mutableStateOf(sessionManager.getStats(StatsFilter.ALL)) }
    var showExportDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(colors.secondary)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back, tint = colors.onSecondary, modifier = Modifier.size(20.dp))
            }
            Text(strings.statsTitle, color = colors.onBackground, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))

            IconButton(
                onClick = { showExportDialog = true },
                modifier = Modifier.size(40.dp).clip(CircleShape).background(colors.primary.copy(alpha = 0.2f))
            ) {
                Icon(Icons.Filled.Share, contentDescription = strings.exportStats, tint = colors.primary, modifier = Modifier.size(20.dp))
            }
        }

        if (stats.sessionCount == 0) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Filled.DateRange, contentDescription = null, tint = colors.mutedForeground, modifier = Modifier.size(64.dp))
                    Text(strings.noData, color = colors.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(strings.noDataDesc, color = colors.mutedForeground, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.primary.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🍣", fontSize = 48.sp)
                            Text(stats.total.toString(), color = colors.primary, fontSize = 56.sp, fontWeight = FontWeight.ExtraBold)
                            Text(strings.totalPiecesLabel, color = colors.mutedForeground, fontSize = 16.sp)
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(Icons.Filled.DateRange, stats.sessionCount.toString(), strings.sessionCount, colors, modifier = Modifier.weight(1f))
                        StatCard(Icons.Filled.Add, String.format("%.1f", stats.avgPerSession), strings.average, colors, modifier = Modifier.weight(1f))
                        StatCard(Icons.Filled.Add, stats.maxInSession.toString(), strings.record, colors, iconTint = colors.primary, modifier = Modifier.weight(1f))
                    }
                }

                item {
                    Text(strings.breakdown, color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            val maxPieces = stats.pieceStats.values.maxOrNull() ?: 1
                            stats.pieceStats.entries.sortedByDescending { it.value }.forEach { (type, count) ->
                                PieceTypeRow(type, count, maxPieces, colors)
                            }
                        }
                    }
                }

                if (stats.sessionCount > 0) {
                    item {
                        Text(strings.curiosities, color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                CuriosityItem("🍚", strings.riceApprox.format(stats.total * 20), colors)
                                stats.pieceStats.maxByOrNull { it.value }?.let {
                                    CuriosityItem(getPieceEmoji(it.key), strings.favoritePiece.format(it.key), colors)
                                }
                                val salmonEst = (stats.pieceStats["nigiri"] ?: 0) / 2
                                if (salmonEst > 0) CuriosityItem("🐟", strings.salmonApprox.format(salmonEst), colors)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = colors.surface,
            title = { Text(strings.exportTitle, color = colors.onSurface, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(strings.exportSelectFormat, color = colors.mutedForeground)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatsExportBtn(Icons.Filled.Edit, strings.exportPdf, "Abrir", colors, onClick = {
                            showExportDialog = false
                            scope.launch {
                                val uri = ExportUtils.exportStatsToPdf(context, stats.pieceStats, stats.total, StatsFilter.ALL, sessionManager.getSessions())
                                uri?.let { ExportUtils.openFile(context, it, "application/pdf") }
                            }
                        }, modifier = Modifier.weight(1f))
                        StatsExportBtn(Icons.Filled.Check, strings.exportExcel, "Abrir", colors, onClick = {
                            showExportDialog = false
                            scope.launch {
                                val uri = ExportUtils.exportStatsToExcel(context, stats.pieceStats, stats.total, StatsFilter.ALL, sessionManager.getSessions())
                                uri?.let { ExportUtils.openFile(context, it, "text/csv") }
                            }
                        }, modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatsExportBtn(Icons.Filled.Share, strings.exportPdf, strings.exportShare, colors, onClick = {
                            showExportDialog = false
                            scope.launch {
                                val uri = ExportUtils.exportStatsToPdf(context, stats.pieceStats, stats.total, StatsFilter.ALL, sessionManager.getSessions())
                                uri?.let { ExportUtils.shareFile(context, it, "application/pdf", strings.exportShare) }
                            }
                        }, modifier = Modifier.weight(1f))
                        StatsExportBtn(Icons.Filled.Share, strings.exportExcel, strings.exportShare, colors, onClick = {
                            showExportDialog = false
                            scope.launch {
                                val uri = ExportUtils.exportStatsToExcel(context, stats.pieceStats, stats.total, StatsFilter.ALL, sessionManager.getSessions())
                                uri?.let { ExportUtils.shareFile(context, it, "text/csv", strings.exportShare) }
                            }
                        }, modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showExportDialog = false }) { Text(strings.cancel, color = colors.mutedForeground) } }
        )
    }
}

@Composable
private fun StatsExportBtn(
    icon: ImageVector, label: String, sublabel: String,
    colors: SushiColors, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.secondary)
            .then(Modifier.clickable(onClick = onClick))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(colors.primary), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = label, tint = colors.onPrimary, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = colors.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(sublabel, color = colors.mutedForeground, fontSize = 10.sp)
    }
}

@Composable
private fun StatCard(
    icon: ImageVector, value: String, label: String, colors: SushiColors,
    modifier: Modifier = Modifier, iconTint: Color = colors.primary
) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = colors.onSurface, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = colors.mutedForeground, fontSize = 12.sp)
        }
    }
}

@Composable
private fun PieceTypeRow(type: String, count: Int, maxCount: Int, colors: SushiColors) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(getPieceEmoji(type), fontSize = 20.sp)
                Text(type.replaceFirstChar { it.uppercase() }, color = colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            Text(count.toString(), color = colors.primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(colors.secondary)) {
            Box(modifier = Modifier.fillMaxWidth(count.toFloat() / maxCount).fillMaxHeight().clip(RoundedCornerShape(3.dp)).background(colors.primary))
        }
    }
}

@Composable
private fun CuriosityItem(emoji: String, text: String, colors: SushiColors) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(colors.secondary), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 20.sp)
        }
        Text(text, color = colors.onSurface, fontSize = 14.sp)
    }
}

private fun getPieceEmoji(type: String): String = when (type.lowercase()) {
    "nigiri" -> "🍣"; "sashimi" -> "🥢"; "maki" -> "🍙"; "temaki" -> "📜"; "gyoza" -> "🥟"; "otro" -> "🍽️"; else -> "🍱"
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DefaultLocale")
private fun shareStats(context: android.content.Context, sessionManager: SessionStorage) {
    val stats = sessionManager.getStats(StatsFilter.ALL)

    val text = buildString {
        appendLine("🍣 Mis estadisticas de Sushi Tracker")
        appendLine("=====================================")
        appendLine()
        appendLine("📊 RESUMEN")
        appendLine("• Total de piezas: ${stats.total}")
        appendLine("• Sesiones completadas: ${stats.sessionCount}")
        appendLine("• Promedio por sesion: ${String.format("%.1f", stats.avgPerSession)}")
        appendLine("• Record personal: ${stats.maxInSession} piezas 🏆")
        appendLine()
        appendLine("🍱 POR TIPO")
        stats.pieceStats.entries.sortedByDescending { it.value }.forEach { (type, count) ->
            appendLine("${getPieceEmoji(type)} ${type.replaceFirstChar { it.uppercase() }}: $count")
        }
        appendLine()
        appendLine("Registrado con Sushi Tracker 🍣")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Mis estadisticas de Sushi Tracker")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir estadisticas"))
}
