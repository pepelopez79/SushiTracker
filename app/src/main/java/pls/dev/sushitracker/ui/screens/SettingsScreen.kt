package pls.dev.sushitracker.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pls.dev.sushitracker.data.*
import pls.dev.sushitracker.ui.theme.*
import pls.dev.sushitracker.utils.ExportUtils
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DefaultLocale")
@Composable
fun SettingsScreen(
    colors: SushiColors,
    strings: AppStrings.Strings,
    currentTheme: AppTheme,
    currentLanguage: AppLanguage,
    onThemeChange: (AppTheme) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settingsManager = remember { AppSettingsManager(context) }
    val sessionManager = remember { SessionStorage(context) }
    val scope = rememberCoroutineScope()

    var showResetDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showCustomPiecesDialog by remember { mutableStateOf(false) }
    var customPieces by remember { mutableStateOf(settingsManager.getCustomPieces()) }

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
                modifier = Modifier.size(40.dp).clip(CircleShape).background(colors.secondary)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = strings.back,
                    tint = colors.onSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = strings.settingsTitle,
                color = colors.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ── APARIENCIA
            item {
                SectionLabel(strings.appearance, colors)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(strings.appTheme, color = colors.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AppTheme.entries.forEach { theme ->
                                ThemeOption(
                                    theme = theme,
                                    isSelected = currentTheme == theme,
                                    colors = colors,
                                    onClick = { onThemeChange(theme) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // ── IDIOMA
            item {
                SectionLabel(strings.language, colors)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppLanguage.entries.forEach { lang ->
                                LanguageOption(
                                    language = lang,
                                    isSelected = currentLanguage == lang,
                                    colors = colors,
                                    onClick = { onLanguageChange(lang) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // ── TUS DATOS
            item {
                SectionLabel(strings.yourData, colors)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Filled.Share,
                            title = strings.exportStats,
                            subtitle = strings.exportStatsSubtitle,
                            colors = colors,
                            onClick = { showExportDialog = true }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = colors.border)
                        SettingsItem(
                            icon = Icons.Filled.Add,
                            title = strings.customPiecesManage,
                            subtitle = strings.customPiecesSubtitle,
                            colors = colors,
                            onClick = { showCustomPiecesDialog = true }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = colors.border)
                        SettingsItem(
                            icon = Icons.Filled.Delete,
                            title = strings.deleteAll,
                            subtitle = strings.deleteAllSubtitle,
                            colors = colors,
                            isDestructive = true,
                            onClick = { showResetDialog = true }
                        )
                    }
                }
            }

            // ── INFORMACIÓN
            item {
                SectionLabel(strings.information, colors)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    val versionName = runCatching {
                        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0.0"
                    }.getOrDefault("1.0.0")
                    Column {
                        SettingsItem(icon = Icons.Filled.Info, title = strings.version, subtitle = versionName, colors = colors)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = colors.border)
                        SettingsItem(icon = Icons.Filled.Edit, title = strings.developedBy, subtitle = "PLS", colors = colors)
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = colors.surface,
            title = { Text(strings.deleteAllConfirmTitle, color = colors.onSurface, fontWeight = FontWeight.Bold) },
            text = { Text(strings.deleteAllConfirmMsg, color = colors.mutedForeground) },
            confirmButton = {
                TextButton(onClick = {
                    sessionManager.deleteAllSessions()
                    showResetDialog = false
                    Toast.makeText(context, strings.dataDeleted, Toast.LENGTH_SHORT).show()
                }) { Text(strings.deleteAllConfirmBtn, color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text(strings.cancel, color = colors.primary) }
            }
        )
    }

    if (showExportDialog) {
        ExportOptionsDialog(
            colors = colors,
            strings = strings,
            onDismiss = { showExportDialog = false },
            onExportPdf = {
                showExportDialog = false
                scope.launch {
                    val s = sessionManager.getStats(StatsFilter.ALL)
                    val uri = ExportUtils.exportStatsToPdf(context, s.pieceStats, s.total, StatsFilter.ALL, sessionManager.getSessions())
                    uri?.let { ExportUtils.openFile(context, it, "application/pdf") }
                }
            },
            onExportExcel = {
                showExportDialog = false
                scope.launch {
                    val s = sessionManager.getStats(StatsFilter.ALL)
                    val uri = ExportUtils.exportStatsToExcel(context, s.pieceStats, s.total, StatsFilter.ALL, sessionManager.getSessions())
                    uri?.let { ExportUtils.openFile(context, it, "text/csv") }
                }
            },
            onSharePdf = {
                showExportDialog = false
                scope.launch {
                    val s = sessionManager.getStats(StatsFilter.ALL)
                    val uri = ExportUtils.exportStatsToPdf(context, s.pieceStats, s.total, StatsFilter.ALL, sessionManager.getSessions())
                    uri?.let { ExportUtils.shareFile(context, it, "application/pdf", strings.exportShare) }
                }
            },
            onShareExcel = {
                showExportDialog = false
                scope.launch {
                    val s = sessionManager.getStats(StatsFilter.ALL)
                    val uri = ExportUtils.exportStatsToExcel(context, s.pieceStats, s.total, StatsFilter.ALL, sessionManager.getSessions())
                    uri?.let { ExportUtils.shareFile(context, it, "text/csv", strings.exportShare) }
                }
            }
        )
    }

    if (showCustomPiecesDialog) {
        CustomPiecesDialog(
            colors = colors,
            strings = strings,
            pieces = customPieces,
            onDismiss = { showCustomPiecesDialog = false },
            onAdd = { piece ->
                settingsManager.addCustomPiece(piece)
                customPieces = settingsManager.getCustomPieces()
            },
            onDelete = { id ->
                settingsManager.removeCustomPiece(id)
                customPieces = settingsManager.getCustomPieces()
            }
        )
    }
}

@Composable
private fun ExportOptionsDialog(
    colors: SushiColors,
    strings: AppStrings.Strings,
    onDismiss: () -> Unit,
    onExportPdf: () -> Unit,
    onExportExcel: () -> Unit,
    onSharePdf: () -> Unit,
    onShareExcel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        title = { Text(strings.exportTitle, color = colors.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(strings.exportSelectFormat, color = colors.mutedForeground)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExportBtn(Icons.Filled.Edit, strings.exportPdf, "Abrir", colors, onExportPdf, Modifier.weight(1f))
                    ExportBtn(Icons.Filled.Check, strings.exportExcel, "Abrir", colors, onExportExcel, Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExportBtn(Icons.Filled.Share, strings.exportPdf, strings.exportShare, colors, onSharePdf, Modifier.weight(1f))
                    ExportBtn(Icons.Filled.Share, strings.exportExcel, strings.exportShare, colors, onShareExcel, Modifier.weight(1f))
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.cancel, color = colors.mutedForeground) } }
    )
}

@Composable
private fun ExportBtn(
    icon: ImageVector, label: String, sublabel: String,
    colors: SushiColors, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.secondary)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(colors.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = colors.onPrimary, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = colors.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(sublabel, color = colors.mutedForeground, fontSize = 10.sp)
    }
}

@Composable
private fun CustomPiecesDialog(
    colors: SushiColors,
    strings: AppStrings.Strings,
    pieces: List<CustomPiece>,
    onDismiss: () -> Unit,
    onAdd: (CustomPiece) -> Unit,
    onDelete: (String) -> Unit
) {
    var newName by remember { mutableStateOf("") }
    var newEmoji by remember { mutableStateOf("🍱") }
    var showNameError by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<CustomPiece?>(null) }
    val emojiOptions = listOf("🍱", "🍣", "🥢", "🍙", "🥟", "🍜", "🥗", "🍤", "🫙", "🥡")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        title = { Text(strings.customPiecesManage, color = colors.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (pieces.isEmpty()) {
                    Text(strings.customPiecesEmpty, color = colors.mutedForeground, fontSize = 13.sp)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        pieces.forEach { piece ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.secondary)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(piece.emoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = piece.name,
                                    color = colors.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { deleteTarget = piece }, modifier = Modifier.size(32.dp)) {
                                    Icon(
                                        Icons.Filled.Delete, contentDescription = strings.delete,
                                        tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(color = colors.border)
                Text(strings.customPieceName, color = colors.mutedForeground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    emojiOptions.forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (newEmoji == emoji) colors.primary.copy(alpha = 0.25f) else colors.secondary)
                                .border(if (newEmoji == emoji) 2.dp else 0.dp, if (newEmoji == emoji) colors.primary else Color.Transparent, CircleShape)
                                .clickable { newEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) { Text(emoji, fontSize = 16.sp) }
                    }
                }
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it; showNameError = false },
                    placeholder = { Text(strings.customPieceNameHint, color = colors.mutedForeground) },
                    isError = showNameError,
                    supportingText = if (showNameError) { { Text(strings.noPieceName, color = MaterialTheme.colorScheme.error) } } else null,
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
                Button(
                    onClick = {
                        if (newName.isBlank()) { showNameError = true }
                        else {
                            onAdd(CustomPiece(id = "custom_${UUID.randomUUID()}", name = newName.trim(), emoji = newEmoji))
                            newName = ""; newEmoji = "🍱"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.addCustomPiece, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.cancel, color = colors.mutedForeground) } }
    )

    deleteTarget?.let { piece ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = colors.surface,
            title = { Text(strings.deleteCustomPiece, color = colors.onSurface, fontWeight = FontWeight.Bold) },
            text = { Text(strings.deleteCustomPieceConfirm.format(piece.name), color = colors.mutedForeground) },
            confirmButton = {
                TextButton(onClick = { onDelete(piece.id); deleteTarget = null }) {
                    Text(strings.delete, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text(strings.cancel, color = colors.primary) }
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String, colors: SushiColors) {
    Text(
        text = text.uppercase(),
        color = colors.mutedForeground,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun ThemeOption(
    theme: AppTheme, isSelected: Boolean, colors: SushiColors,
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val themeColors = getColorsForTheme(theme)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colors.primary.copy(alpha = 0.1f) else colors.secondary)
            .border(if (isSelected) 2.dp else 0.dp, if (isSelected) colors.primary else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(themeColors.background).border(1.dp, themeColors.border, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(themeColors.primary))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (theme) { AppTheme.DARK -> "Oscuro"; AppTheme.SALMON -> "Salmón"; AppTheme.LIGHT -> "Claro" },
            color = if (isSelected) colors.primary else colors.onSurface,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun LanguageOption(
    language: AppLanguage, isSelected: Boolean, colors: SushiColors,
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colors.primary.copy(alpha = 0.1f) else colors.secondary)
            .border(if (isSelected) 2.dp else 0.dp, if (isSelected) colors.primary else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(language.flag, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = language.displayName,
            color = if (isSelected) colors.primary else colors.onSurface,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector, title: String, subtitle: String, colors: SushiColors,
    isDestructive: Boolean = false, onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon, contentDescription = null,
            tint = if (isDestructive) MaterialTheme.colorScheme.error else colors.mutedForeground,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isDestructive) MaterialTheme.colorScheme.error else colors.onSurface,
                fontSize = 16.sp, fontWeight = FontWeight.Medium
            )
            Text(text = subtitle, color = colors.mutedForeground, fontSize = 13.sp)
        }
        if (onClick != null) {
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = colors.mutedForeground, modifier = Modifier.size(20.dp))
        }
    }
}