package pls.dev.sushitracker.ui.screens

import android.os.Build
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
import pls.dev.sushitracker.data.SUSHI_PIECES
import pls.dev.sushitracker.data.SessionRecord
import pls.dev.sushitracker.data.SessionStorage
import pls.dev.sushitracker.ui.components.PieceCounterItem
import pls.dev.sushitracker.ui.theme.*
import java.util.UUID

enum class CounterPhase {
    RESTAURANT_INPUT, COUNTING, CONFIRM_SAVE
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CounterScreen(onBack: () -> Unit, colors: SushiColors) {
    val context = LocalContext.current
    val storage = remember { SessionStorage(context) }

    var phase by remember { mutableStateOf(CounterPhase.RESTAURANT_INPUT) }
    var restaurant by remember { mutableStateOf("") }
    var counts by remember {
        mutableStateOf(SUSHI_PIECES.associate { it.id to 0 })
    }

    val totalPieces = counts.values.sum()

    when (phase) {
        CounterPhase.RESTAURANT_INPUT -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nueva sesión",
                            color = colors.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¿Dónde estás comiendo?",
                            color = colors.mutedForeground,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = restaurant,
                            onValueChange = { restaurant = it },
                            placeholder = {
                                Text(
                                    "Nombre del restaurante...",
                                    color = colors.mutedForeground
                                )
                            },
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.secondary,
                                    contentColor = colors.onSecondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(text = "Cancelar", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { phase = CounterPhase.COUNTING },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.primary,
                                    contentColor = colors.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(text = "Empezar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        CounterPhase.COUNTING -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
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
                            text = restaurant.ifEmpty { "Sin nombre" },
                            color = colors.onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(colors.primary.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Total: ",
                                    color = colors.primary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$totalPieces",
                                    color = colors.primary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 96.dp, top = 8.dp)
                    ) {
                        items(SUSHI_PIECES) { piece ->
                            PieceCounterItem(
                                piece = piece,
                                count = counts[piece.id] ?: 0,
                                onIncrement = {
                                    counts = counts.toMutableMap().apply {
                                        this[piece.id] = (this[piece.id] ?: 0) + 1
                                    }
                                },
                                onDecrement = {
                                    counts = counts.toMutableMap().apply {
                                        val current = this[piece.id] ?: 0
                                        this[piece.id] = maxOf(0, current - 1)
                                    }
                                }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(colors.background.copy(alpha = 0.95f))
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { phase = CounterPhase.CONFIRM_SAVE },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.secondary,
                            contentColor = colors.onSecondary
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = "TERMINAR SESIÓN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        CounterPhase.CONFIRM_SAVE -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¿Terminar sesión?",
                            color = colors.onSurface,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$totalPieces",
                            color = colors.primary,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "piezas en total",
                            color = colors.mutedForeground,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { phase = CounterPhase.COUNTING },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.secondary,
                                    contentColor = colors.onSecondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Seguir", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    val session = SessionRecord(
                                        id = UUID.randomUUID().toString(),
                                        date = java.time.LocalDateTime.now()
                                            .format(java.time.format.DateTimeFormatter.ISO_DATE_TIME),
                                        restaurant = restaurant.ifEmpty { "Sin nombre" },
                                        pieces = counts,
                                        totalPieces = totalPieces
                                    )
                                    storage.saveSession(session)
                                    onBack()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.primary,
                                    contentColor = colors.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Guardar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
