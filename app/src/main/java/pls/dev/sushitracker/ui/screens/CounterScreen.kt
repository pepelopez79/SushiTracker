package pls.dev.sushitracker.ui.screens

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pls.dev.sushitracker.data.*
import pls.dev.sushitracker.ui.components.CustomPieceCounterItem
import pls.dev.sushitracker.ui.components.PieceCounterItem
import pls.dev.sushitracker.ui.theme.*
import java.util.UUID

enum class CounterPhase { RESTAURANT_INPUT, COUNTING, CONFIRM_SAVE }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CounterScreen(
    onBack: () -> Unit,
    colors: SushiColors,
    strings: AppStrings.Strings
) {
    val context = LocalContext.current
    val storage = remember { SessionStorage(context) }
    val settingsManager = remember { AppSettingsManager(context) }
    val customPieces = remember { settingsManager.getCustomPieces() }

    var phase by remember { mutableStateOf(CounterPhase.RESTAURANT_INPUT) }
    var restaurant by remember { mutableStateOf("") }

    var counts by remember {
        mutableStateOf(
            SUSHI_PIECES.associate { it.id to 0 } +
                    customPieces.associate { it.id to 0 }
        )
    }
    var showExitDialog by remember { mutableStateOf(false) }

    val totalPieces = counts.values.sum()

    BackHandler(enabled = true) {
        if (phase != CounterPhase.RESTAURANT_INPUT && totalPieces > 0) {
            showExitDialog = true
        } else {
            onBack()
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            containerColor = colors.surface,
            title = {
                Text(
                    strings.exitDialogTitle,
                    color = colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    strings.exitDialogMessage,
                    color = colors.mutedForeground
                )
            },
            confirmButton = {
                TextButton(onClick = onBack) {
                    Text(strings.exit, color = colors.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(strings.continueStr, color = colors.mutedForeground)
                }
            }
        )
    }

    when (phase) {
        CounterPhase.RESTAURANT_INPUT -> {
            Box(
                modifier = Modifier.fillMaxSize().background(colors.background),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(strings.newSession, color = colors.onSurface, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(strings.whereAreYouEating, color = colors.mutedForeground, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = restaurant,
                            onValueChange = { restaurant = it },
                            placeholder = { Text(strings.restaurantName, color = colors.mutedForeground) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.border,
                                cursorColor = colors.primary,
                                focusedTextColor = colors.onSurface,
                                unfocusedTextColor = colors.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(containerColor = colors.secondary, contentColor = colors.onSecondary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) { Text(strings.cancel, fontWeight = FontWeight.Bold) }
                            Button(
                                onClick = { phase = CounterPhase.COUNTING },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) { Text(strings.start, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }

        CounterPhase.COUNTING -> {
            Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
                Column(modifier = Modifier.fillMaxSize()) {
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
                            text = restaurant.ifEmpty { strings.noName },
                            color = colors.onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(50)).background(colors.primary.copy(alpha = 0.2f)).padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${strings.total}: ", color = colors.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("$totalPieces", color = colors.primary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 96.dp, top = 8.dp)
                    ) {
                        items(SUSHI_PIECES) { piece ->
                            PieceCounterItem(
                                piece = piece,
                                count = counts[piece.id] ?: 0,
                                onIncrement = { counts = counts.toMutableMap().apply { this[piece.id] = (this[piece.id] ?: 0) + 1 } },
                                onDecrement = { counts = counts.toMutableMap().apply { this[piece.id] = maxOf(0, (this[piece.id] ?: 0) - 1) } }
                            )
                        }
                        if (customPieces.isNotEmpty()) {
                            item {
                                Text(
                                    text = "— Personalizadas —",
                                    color = colors.mutedForeground,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                            items(customPieces) { piece ->
                                CustomPieceCounterItem(
                                    piece = piece,
                                    count = counts[piece.id] ?: 0,
                                    colors = colors,
                                    onIncrement = { counts = counts.toMutableMap().apply { this[piece.id] = (this[piece.id] ?: 0) + 1 } },
                                    onDecrement = { counts = counts.toMutableMap().apply { this[piece.id] = maxOf(0, (this[piece.id] ?: 0) - 1) } }
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(colors.background.copy(alpha = 0.95f)).padding(16.dp)
                ) {
                    Button(
                        onClick = { phase = CounterPhase.CONFIRM_SAVE },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.secondary, contentColor = colors.onSecondary),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text(strings.endSession, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        CounterPhase.CONFIRM_SAVE -> {
            Box(
                modifier = Modifier.fillMaxSize().background(colors.background),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(strings.finishSession, color = colors.onSurface, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("$totalPieces", color = colors.primary, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
                        Text(strings.totalPieces, color = colors.mutedForeground, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { phase = CounterPhase.COUNTING },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.secondary, contentColor = colors.onSecondary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) { Text(strings.continueStr, fontWeight = FontWeight.Bold) }
                            Button(
                                onClick = {
                                    val session = SessionRecord(
                                        id = UUID.randomUUID().toString(),
                                        date = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME),
                                        restaurant = restaurant.ifEmpty { strings.noName },
                                        pieces = counts,
                                        totalPieces = totalPieces
                                    )
                                    storage.saveSession(session)
                                    onBack()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) { Text(strings.save, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }
    }
}