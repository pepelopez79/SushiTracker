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
import pls.dev.sushitracker.data.SessionRecord
import pls.dev.sushitracker.data.SessionStorage
import pls.dev.sushitracker.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.collections.filter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    colors: SushiColors,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionStorage(context) }
    var sessions by remember { mutableStateOf(sessionManager.getSessions()) }
    var expandedSessionId by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf<SessionRecord?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.secondary)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = colors.onSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Historial",
                color = colors.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )

            if (sessions.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.primary.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${sessions.size} sesiones",
                        color = colors.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        tint = colors.mutedForeground,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Sin historial",
                        color = colors.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Completa tu primera sesión\npara ver tu historial aqui",
                        color = colors.mutedForeground,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
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
                        onToggleExpand = {
                            expandedSessionId = if (expandedSessionId == session.id) null else session.id
                        },
                        onShare = {
                            shareSession(context, session)
                        },
                        onDelete = {
                            showDeleteDialog = session
                        }
                    )
                }
            }
        }
    }

    showDeleteDialog?.let { session ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            containerColor = colors.surface,
            title = {
                Text(
                    "¿Eliminar sesion?",
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Se eliminara la sesion del ${formatDate(session.date)} con ${session.totalPieces} piezas.",
                    color = colors.mutedForeground
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        sessionManager.deleteSession(session.id)
                        sessions = sessionManager.getSessions()
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar", color = colors.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar", color = colors.mutedForeground)
                }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colors.primary.copy(alpha = 0.2f)
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍣",
                        fontSize = 24.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatDate(session.date),
                        color = colors.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = session.restaurant,
                        color = colors.mutedForeground,
                        fontSize = 12.sp
                    )
                    Text(
                        text = getRelativeDate(session.date),
                        color = colors.mutedForeground,
                        fontSize = 12.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = totalPieces.toString(),
                        color = colors.primary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "piezas",
                        color = colors.mutedForeground,
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                    tint = colors.mutedForeground,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = colors.border
                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        session.pieces
                            .filter { it.value > 0 }
                            .forEach { piece ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = getPieceEmoji(piece.key),
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = piece.key.replaceFirstChar { it.uppercase() },
                                            color = colors.onSurface,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Text(
                                        text = "${piece.value}",
                                        color = colors.primary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onShare,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colors.primary
                            )
                        ) {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Compartir", fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Eliminar", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun getPieceEmoji(type: String): String {
    return when (type.lowercase()) {
        "nigiri" -> "🍣"
        "sashimi" -> "🥢"
        "maki" -> "🍙"
        "temaki" -> "📜"
        "gyoza" -> "🥟"
        "otro" -> "🍽️"
        else -> "🍱"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun formatDate(dateString: String): String {
    return try {
        val date = try {
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME).toLocalDate()
        } catch (e: Exception) {
            LocalDate.parse(dateString)
        }
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", java.util.Locale("es", "ES"))
        date.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getRelativeDate(dateString: String): String {
    return try {
        val date = try {
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME).toLocalDate()
        } catch (e: Exception) {
            LocalDate.parse(dateString)
        }
        val today = LocalDate.now()
        val daysBetween = ChronoUnit.DAYS.between(date, today)

        when {
            daysBetween == 0L -> "Hoy"
            daysBetween == 1L -> "Ayer"
            daysBetween < 7 -> "Hace $daysBetween dias"
            daysBetween < 30 -> "Hace ${daysBetween / 7} semanas"
            else -> "Hace ${daysBetween / 30} meses"
        }
    } catch (e: Exception) {
        ""
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun shareSession(context: android.content.Context, session: SessionRecord) {
    val totalPieces = session.totalPieces
    val text = buildString {
        appendLine("🍣 Mi sesión de sushi")
        appendLine("📅 ${formatDate(session.date)}")
        appendLine("🏠 ${session.restaurant}")
        appendLine()
        appendLine("Total: $totalPieces piezas")
        appendLine()
        session.pieces
            .filter { it.value > 0 }
            .forEach { piece ->
                appendLine("${getPieceEmoji(piece.key)} ${piece.key.replaceFirstChar { it.uppercase() }}: ${piece.value}")
            }
        appendLine()
        appendLine("Registrado con Sushi Tracker 🍣")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Mi sesion de sushi - ${formatDate(session.date)}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir sesión"))
}
