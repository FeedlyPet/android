package com.example.feedlypet.ui.auth.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CaramelColor = Color(0xFFC4875A)

@Composable
fun PawLogo(modifier: Modifier = Modifier) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // Light theme: brown paw. Dark theme: white/light paw
    val toeColor1 = if (isDark) Color.White else Color(0xFFD49A6A)
    val toeColor2 = if (isDark) Color(0xFFCCCCCC) else Color(0xFF7B3518)
    val padColor1 = if (isDark) Color.White else Color(0xFF5C2A0D)
    val padColor2 = if (isDark) Color(0xFFDDDDDD) else Color(0xFF1E0A02)
    val highlightColor = if (isDark) Color.White.copy(alpha = 0.45f) else Color(0xFFE8BF90).copy(alpha = 0.55f)
    val arcColor = if (isDark) Color(0xFF8B4513) else CaramelColor
    val wordmarkMain = if (isDark) Color.White else Color(0xFF2C1208)
    val wordmarkAccent = CaramelColor

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.size(96.dp)) {
            val s = size.width / 80f

            drawToe(s, cx = 19f, cy = 30f, angle = -28f, color1 = toeColor1, color2 = toeColor2, highlight = highlightColor)
            drawToe(s, cx = 33f, cy = 19f, angle = -8f,  color1 = toeColor1, color2 = toeColor2, highlight = highlightColor)
            drawToe(s, cx = 48f, cy = 19f, angle = 8f,   color1 = toeColor1, color2 = toeColor2, highlight = highlightColor)
            drawToe(s, cx = 62f, cy = 30f, angle = 28f,  color1 = toeColor1, color2 = toeColor2, highlight = highlightColor)

            val padPath = Path().apply {
                moveTo(14 * s, 51 * s)
                cubicTo(14 * s, 40 * s, 26 * s, 36 * s, 40 * s, 36 * s)
                cubicTo(54 * s, 36 * s, 66 * s, 40 * s, 66 * s, 51 * s)
                cubicTo(66 * s, 62 * s, 55 * s, 72 * s, 40 * s, 72 * s)
                cubicTo(25 * s, 72 * s, 14 * s, 62 * s, 14 * s, 51 * s)
                close()
            }
            drawPath(padPath, brush = Brush.radialGradient(
                colors = listOf(padColor1, padColor2),
                center = Offset(40 * s, 54 * s),
                radius = 36 * s
            ))

            val arc1 = Path().apply {
                moveTo(35 * s, 54 * s)
                quadraticBezierTo(40 * s, 49.5f * s, 45 * s, 54 * s)
            }
            drawPath(arc1, color = arcColor, style = Stroke(width = 2.6f * s, cap = StrokeCap.Round))

            val arc2 = Path().apply {
                moveTo(29.5f * s, 60 * s)
                quadraticBezierTo(40 * s, 52 * s, 50.5f * s, 60 * s)
            }
            drawPath(arc2, color = arcColor.copy(alpha = 0.55f), style = Stroke(width = 2.4f * s, cap = StrokeCap.Round))

            val arc3 = Path().apply {
                moveTo(24 * s, 66 * s)
                quadraticBezierTo(40 * s, 55 * s, 56 * s, 66 * s)
            }
            drawPath(arc3, color = arcColor.copy(alpha = 0.28f), style = Stroke(width = 2.1f * s, cap = StrokeCap.Round))

            drawCircle(color = arcColor, radius = 2.8f * s, center = Offset(40 * s, 50 * s))
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = wordmarkMain, fontWeight = FontWeight.ExtraBold)) {
                    append("Feedly")
                }
                withStyle(SpanStyle(color = wordmarkAccent, fontWeight = FontWeight.ExtraBold)) {
                    append("Pet")
                }
            },
            fontSize = 32.sp,
            letterSpacing = (-0.5).sp
        )
    }
}

private fun DrawScope.drawToe(
    s: Float, cx: Float, cy: Float, angle: Float,
    color1: Color, color2: Color, highlight: Color
) {
    withTransform({ rotate(angle, pivot = Offset(cx * s, cy * s)) }) {
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(color1, color2),
                center = Offset(cx * s, (cy - 2) * s),
                radius = 9 * s
            ),
            topLeft = Offset((cx - 7) * s, (cy - 8.5f) * s),
            size = Size(14 * s, 17 * s)
        )
        drawOval(
            color = highlight,
            topLeft = Offset((cx - 3.2f) * s, (cy - 6.5f) * s),
            size = Size(6.4f * s, 8 * s)
        )
    }
}
