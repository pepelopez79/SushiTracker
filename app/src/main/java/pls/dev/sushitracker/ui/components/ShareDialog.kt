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
import pls.dev.sushitracker.ui.theme.*
import pls.dev.sushitracker.utils.ShareUtils

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShareSessionDialog(
    session: SessionRecord,
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
                    text = "Compartir sesion",
                    color = OnSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${session.totalPieces} piezas en ${session.restaurant}",
                    color = MutedForeground,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ShareOption(
                        icon = Icons.Filled.Add,
                        label = "Texto",
                        onClick = {
                            val text = ShareUtils.generateSessionShareText(session)
                            ShareUtils.shareText(context, text)
                            onDismiss()
                        }
                    )

                    ShareOption(
                        icon = Icons.Filled.AccountBox,
                        label = "Imagen",
                        onClick = {
                            val uri = ShareUtils.generateSessionImage(context, session)
                            uri?.let {
                                ShareUtils.shareImage(context, it)
                            }
                            onDismiss()
                        }
                    )

                    ShareOption(
                        icon = Icons.Filled.AccountCircle,
                        label = "WhatsApp",
                        onClick = {
                            val text = ShareUtils.generateSessionShareText(session)
                            ShareUtils.shareText(context, text, "Compartir en WhatsApp")
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
fun ShareOption(
    icon: ImageVector,
    label: String,
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
            fontSize = 12.sp
        )
    }
}
