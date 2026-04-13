package pls.dev.sushitracker.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pls.dev.sushitracker.data.SessionRecord
import pls.dev.sushitracker.ui.theme.*
import pls.dev.sushitracker.data.SessionStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class HistoryTab { HISTORIAL, RANKING }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val storage = remember { SessionStorage(context) }

    var tab by remember { mutableStateOf(HistoryTab.HISTORIAL) }
    var sessions by remember { mutableStateOf(storage.getSessions()) }
    var ranking by remember { mutableStateOf(storage.getRanking(10)) }
    var selectedSession by remember { mutableStateOf<SessionRecord?>(null) }

    selectedSession?.let { session ->
        SessionDetailScreen(
            session = session,
            onBack = { selectedSession = null }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
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
                    .background(Secondary)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = OnSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Historial / Ranking",
                color = OnBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(50))
                .background(Secondary)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (tab == HistoryTab.HISTORIAL) Primary else Color.Transparent)
                    .clickable { tab = HistoryTab.HISTORIAL }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "HISTORIAL",
                    color = if (tab == HistoryTab.HISTORIAL) OnPrimary else OnSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (tab == HistoryTab.RANKING) Primary else Color.Transparent)
                    .clickable { tab = HistoryTab.RANKING }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RANKING",
                    color = if (tab == HistoryTab.RANKING) OnPrimary else OnSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        when (tab) {
            HistoryTab.HISTORIAL -> {
                if (sessions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.DateRange,
                                contentDescription = null,
                                tint = MutedForeground.copy(alpha = 0.4f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Sin sesiones",
                                color = MutedForeground,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Comienza una sesi\u00f3n para ver tu historial",
                                color = MutedForeground.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        itemsIndexed(sessions) { _, session ->
                            val formattedDate = formatSessionDate(session.date)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSession = session },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = formattedDate,
                                            color = MutedForeground,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = session.restaurant,
                                            color = OnSurface,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(Primary.copy(alpha = 0.2f))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${session.totalPieces}",
                                                color = Primary,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "pzs",
                                                color = Primary,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HistoryTab.RANKING -> {
                if (ranking.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                tint = MutedForeground.copy(alpha = 0.4f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Sin datos de ranking",
                                color = MutedForeground,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Completa sesiones para ver tu ranking",
                                color = MutedForeground.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        itemsIndexed(ranking) { index, session ->
                            val formattedDate = formatSessionDate(session.date)
                            val medalColor = getMedalColor(index)
                            val medalBgAlpha = if (index < 3) 0.1f else 0f

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSession = session },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (index < 3) medalColor.copy(alpha = medalBgAlpha) else Surface
                                ),
                                border = if (index < 3) {
                                    CardDefaults.outlinedCardBorder().copy(
                                    )
                                } else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (index < 3) {
                                                Modifier.background(
                                                    medalColor.copy(alpha = 0.1f),
                                                    RoundedCornerShape(16.dp)
                                                )
                                            } else Modifier
                                        )
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (index < 3) medalColor else Secondary
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            color = if (index < 3) Background else OnSecondary,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "$formattedDate - ${session.restaurant}",
                                            color = MutedForeground,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${session.totalPieces} piezas",
                                            color = OnBackground,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatSessionDate(dateStr: String): String {
    return try {
        val dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME)
        dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        dateStr
    }
}

private fun getMedalColor(index: Int): Color {
    return when (index) {
        0 -> Gold
        1 -> Silver
        2 -> Bronze
        else -> Secondary
    }
}
