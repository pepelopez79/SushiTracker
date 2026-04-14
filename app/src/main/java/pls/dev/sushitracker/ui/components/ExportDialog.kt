package pls.dev.sushitracker.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pls.dev.sushitracker.data.SessionRecord
import pls.dev.sushitracker.data.StatsFilter
import pls.dev.sushitracker.ui.theme.*
import pls.dev.sushitracker.utils.ExportUtils

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExportStatsDialog(
    pieceStats: Map<String, Int>,
    total: Int,
    filter: StatsFilter,
    sessions: List<SessionRecord>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Exportar estadisticas",
                    color = OnSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Elige el formato de exportacion",
                    color = MutedForeground,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ExportOption(
                        icon = Icons.Filled.Edit,
                        label = "PDF",
                        sublabel = "Documento",
                        onClick = {
                            val uri = ExportUtils.exportStatsToPdf(
                                context, pieceStats, total, filter, sessions
                            )
                            uri?.let {
                                ExportUtils.openFile(context, it, "application/pdf")
                            }
                            onDismiss()
                        }
                    )

                    ExportOption(
                        icon = Icons.Filled.Check,
                        label = "Excel",
                        sublabel = "CSV",
                        onClick = {
                            val uri = ExportUtils.exportStatsToExcel(
                                context, pieceStats, total, filter, sessions
                            )
                            uri?.let {
                                ExportUtils.openFile(context, it, "text/csv")
                            }
                            onDismiss()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ExportOption(
                        icon = Icons.Filled.Share,
                        label = "Compartir",
                        sublabel = "PDF",
                        onClick = {
                            val uri = ExportUtils.exportStatsToPdf(
                                context, pieceStats, total, filter, sessions
                            )
                            uri?.let {
                                ExportUtils.shareFile(context, it, "application/pdf", "Compartir PDF")
                            }
                            onDismiss()
                        }
                    )

                    ExportOption(
                        icon = Icons.Filled.Share,
                        label = "Compartir",
                        sublabel = "Excel",
                        onClick = {
                            val uri = ExportUtils.exportStatsToExcel(
                                context, pieceStats, total, filter, sessions
                            )
                            uri?.let {
                                ExportUtils.shareFile(context, it, "text/csv", "Compartir Excel")
                            }
                            onDismiss()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancelar",
                        color = MutedForeground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ExportOption(
    icon: ImageVector,
    label: String,
    sublabel: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = OnPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = OnSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = sublabel,
            color = MutedForeground,
            fontSize = 10.sp
        )
    }
}
