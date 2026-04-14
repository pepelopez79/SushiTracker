package pls.dev.sushitracker.ui.screens

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pls.dev.sushitracker.data.AppStrings
import pls.dev.sushitracker.data.SessionRecord
import pls.dev.sushitracker.data.SessionStorage
import pls.dev.sushitracker.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    colors: SushiColors,
    strings: AppStrings.Strings,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionStorage(context) }
    var sessions by remember { mutableStateOf(sessionManager.getSessions()) }
    var expandedSessionId by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf<SessionRecord?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(colors.background)
    ) {
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
            Text(
                text = strings.historyTitle,
                color = colors.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
            if (sessions.isNotEmpty()) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(colors.primary.copy(alpha = 0.2f)).padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("${sessions.size} ${strings.sessions}", color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (sessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = colors.mutedForeground, modifier = Modifier.size(64.dp))
                    Text(strings.noHistory, color = colors.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(strings.noHistoryDesc, color = colors.mutedForeground, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(sessions, key = { it.id }) { session ->
                    SessionHistoryCard(
                        session = session,
                        isExpanded = expandedSessionId == session.id,
                        colors = colors,
                        strings = strings,
                        onToggleExpand = { expandedSessionId = if (expandedSessionId == session.id) null else session.id },
                        onShare = { shareSession(context, session, strings) },
                        onDelete = { showDeleteDialog = session }
                    )
                }
            }
        }
    }

    showDeleteDialog?.let { session ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            containerColor = colors.surface,
            title = { Text(strings.deleteSession, color = colors.onSurface, fontWeight = FontWeight.Bold) },
            text = { Text(strings.deleteSessionConfirm.format(session.totalPieces), color = colors.mutedForeground) },
            confirmButton = {
                TextButton(onClick = {
                    sessionManager.deleteSession(session.id)
                    sessions = sessionManager.getSessions()
                    showDeleteDialog = null
                }) { Text(strings.delete, color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text(strings.cancel, color = colors.primary) }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SessionHistoryCard(
    session: SessionRecord,
    isExpanded: Boolean,
    colors: SushiColors,
    strings: AppStrings.Strings,
    onToggleExpand: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val totalPieces = session.totalPieces

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onToggleExpand).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(when { totalPieces >= 50 -> colors.primary.copy(alpha = 0.2f); totalPieces >= 30 -> colors.primary.copy(alpha = 0.2f); else -> colors.secondary }),
                    contentAlignment = Alignment.Center
                ) { Text("🍣", fontSize = 24.sp) }

                Column(modifier = Modifier.weight(1f)) {
                    Text(formatDate(session.date), color = colors.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(session.restaurant, color = colors.mutedForeground, fontSize = 12.sp)
                    Text(getRelativeDate(session.date, strings), color = colors.mutedForeground, fontSize = 11.sp)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = totalPieces.toString(),
                        color = when { totalPieces >= 50 -> colors.primary; totalPieces >= 30 -> colors.primary; else -> colors.onSurface },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(strings.pieces, color = colors.mutedForeground, fontSize = 11.sp)
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.mutedForeground,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = colors.border)
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        session.pieces.filter { it.value > 0 }.forEach { (key, value) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(getPieceEmoji(key), fontSize = 16.sp)
                                    Text(key.replaceFirstChar { it.uppercase() }, color = colors.onSurface, fontSize = 14.sp)
                                }
                                Text("$value", color = colors.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onShare,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.share, fontSize = 13.sp)
                        }
                        OutlinedButton(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.delete, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun getPieceEmoji(type: String): String = when (type.lowercase()) {
    "nigiri" -> "🍣"; "sashimi" -> "🥢"; "maki" -> "🍙"; "temaki" -> "📜"; "gyoza" -> "🥟"; "otro" -> "🍽️"; else -> "🍱"
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun formatDate(dateString: String): String {
    return try {
        val date = try {
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME).toLocalDate()
        } catch (e: Exception) { LocalDate.parse(dateString) }
        date.format(DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", java.util.Locale("es", "ES")))
    } catch (e: Exception) { dateString }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getRelativeDate(dateString: String, strings: AppStrings.Strings): String {
    return try {
        val date = try {
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME).toLocalDate()
        } catch (e: Exception) { LocalDate.parse(dateString) }
        val daysBetween = ChronoUnit.DAYS.between(date, LocalDate.now())
        when {
            daysBetween == 0L -> strings.today
            daysBetween == 1L -> strings.yesterday
            daysBetween < 7 -> strings.daysAgo.format(daysBetween)
            daysBetween < 30 -> strings.weeksAgo.format(daysBetween / 7)
            else -> strings.monthsAgo.format(daysBetween / 30)
        }
    } catch (e: Exception) { "" }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun shareSession(context: android.content.Context, session: SessionRecord, strings: AppStrings.Strings) {
    val text = buildString {
        appendLine("🍣 ${strings.newSession}")
        appendLine("📅 ${formatDate(session.date)}")
        appendLine("🏠 ${session.restaurant}")
        appendLine()
        appendLine("${strings.total}: ${session.totalPieces} ${strings.pieces}")
        appendLine()
        session.pieces.filter { it.value > 0 }.forEach { (key, value) ->
            appendLine("${getPieceEmoji(key)} ${key.replaceFirstChar { it.uppercase() }}: $value")
        }
        appendLine()
        appendLine("Sushi Tracker 🍣")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Sushi Tracker - ${formatDate(session.date)}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, strings.share))
}