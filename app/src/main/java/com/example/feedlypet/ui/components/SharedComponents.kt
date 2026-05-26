package com.example.feedlypet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feedlypet.R
import com.example.feedlypet.ui.common.UiText
import com.example.feedlypet.ui.theme.AmberLevel
import com.example.feedlypet.ui.theme.GreenOnline
import com.example.feedlypet.ui.theme.RedLevel


@Composable
fun AppSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
            actionColor = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun StatusChip(isOnline: Boolean) {
    val (color, label) = if (isOnline)
        Pair(GreenOnline, stringResource(R.string.devices_status_online))
    else
        Pair(Color(0xFF9E9E9E), stringResource(R.string.devices_status_offline))

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun FoodLevelBar(level: Int, modifier: Modifier = Modifier) {
    val fillColor = when {
        level >= 50 -> GreenOnline
        level >= 20 -> AmberLevel
        else -> RedLevel
    }
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        val radius = size.height / 2
        drawRoundRect(color = trackColor, cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius))
        if (level > 0) {
            drawRoundRect(
                color = fillColor,
                size = size.copy(width = size.width * (level / 100f)),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius)
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: UiText, onRetry: () -> Unit) {
    ErrorScreen(message.resolve(LocalContext.current), onRetry)
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        OutlinedButton(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(R.string.common_retry))
        }
    }
}

@Composable
fun EmptyScreen(emoji: String, text: String) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 48.sp)
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructive: Boolean = false
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = if (destructive) ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ) else ButtonDefaults.buttonColors()
            ) { Text(confirmLabel ?: context.getString(R.string.common_ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(context.getString(R.string.common_cancel)) }
        }
    )
}

fun speciesEmoji(species: String): String = when (species.lowercase()) {
    "cat" -> "🐱"
    "dog" -> "🐶"
    "rabbit", "bunny" -> "🐰"
    "bird", "parrot" -> "🐦"
    "fish" -> "🐟"
    "hamster" -> "🐹"
    "turtle" -> "🐢"
    else -> "🐾"
}

/** For event timestamps — server stores in local time despite Z suffix, display as-is */
fun formatTimestamp(iso: String): String {
    return try {
        val instant = java.time.Instant.parse(if (iso.endsWith("Z") || iso.contains("+")) iso else "${iso}Z")
        java.time.LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC)
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    } catch (e: Exception) {
        iso.replace("T", " ").take(16)
    }
}

/** For device lastSeen — real UTC timestamp, convert to Europe/Kiev (UTC+3) */
fun formatUtcTimestamp(iso: String): String {
    return try {
        val instant = java.time.Instant.parse(if (iso.endsWith("Z") || iso.contains("+")) iso else "${iso}Z")
        instant.atZone(java.time.ZoneId.of("Europe/Kiev"))
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    } catch (e: Exception) {
        iso.replace("T", " ").take(16)
    }
}
