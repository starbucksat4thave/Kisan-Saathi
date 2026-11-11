package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BarkatGold
import com.example.ui.theme.FieldGreen
import com.example.ui.theme.NotoNastaliqUrdu
import com.example.ui.theme.WheatGold
import com.example.util.LocalAppLanguage
import com.example.util.isUrdu

@Composable
fun BarkatSceneView(
    barkatMaunds: Double,
    onTapDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    val stage = when {
        barkatMaunds >= 60.0 -> 4
        barkatMaunds >= 30.0 -> 3
        barkatMaunds >= 15.0 -> 2
        barkatMaunds >= 5.0 -> 1
        else -> 0
    }

    val animatedMaunds by animateFloatAsState(
        targetValue = barkatMaunds.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "barkat_maunds_anim"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .clickable { onTapDetails() }
            .testTag("barkat_scene_card"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column {
            // Scene Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    drawHarvestLandscape(stage)
                }

                // Stage Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = BarkatGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (stage) {
                                4 -> if (isUrdu) "مرحلہ ۵: خوشحال گھرانہ اور ٹریکٹر" else "Stage 5: Prosperous Home & Tractor"
                                3 -> if (isUrdu) "مرحلہ ۴: پکی فصل اور مویشی" else "Stage 4: Ripe Crop & Cattle"
                                2 -> if (isUrdu) "مرحلہ ۳: پانی کا تالاب اور ہریالی" else "Stage 3: Rainwater Pond & Lush Field"
                                1 -> if (isUrdu) "مرحلہ ۲: سبز کونپلیں اور گائے" else "Stage 2: Green Shoots & Dairy Cow"
                                else -> if (isUrdu) "مرحلہ ۱: بوائی کی ابتدا" else "Stage 1: Sowing & Seedbed"
                            },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Bottom Barkat Summary Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isUrdu) "بچائی گئی برکت: " else "Saved Barkat: ",
                            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                            fontSize = if (isUrdu) 18.sp else 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isUrdu) "%.1f من".format(animatedMaunds) else "%.1f maunds".format(animatedMaunds),
                            fontSize = if (isUrdu) 20.sp else 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = FieldGreen
                        )
                    }
                    Text(
                        text = if (isUrdu) "(۱ من = ۴۰ کلوگرام گندم)" else "(1 maund = 40 kg wheat)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(BarkatGold.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Details",
                        tint = WheatGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isUrdu) "اندازہ (تفصیل دیکھیں)" else "Estimate (Details)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawHarvestLandscape(stage: Int) {
    val width = size.width
    val height = size.height

    // 1. Sky Gradient
    val skyBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF81D4FA), Color(0xFFE1F5FE)),
        startY = 0f,
        endY = height * 0.6f
    )
    drawRect(brush = skyBrush, size = Size(width, height * 0.65f))

    // Sun
    drawCircle(
        color = Color(0xFFFFEE58),
        radius = 24.dp.toPx(),
        center = Offset(width * 0.85f, height * 0.22f)
    )

    // Soft clouds
    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 18.dp.toPx(), center = Offset(width * 0.25f, height * 0.18f))
    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 24.dp.toPx(), center = Offset(width * 0.32f, height * 0.16f))
    drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 16.dp.toPx(), center = Offset(width * 0.38f, height * 0.20f))

    // 2. Distant Pothohar Ridge (Margalla / Salt Range foothills)
    val hillPath = Path().apply {
        moveTo(0f, height * 0.55f)
        cubicTo(width * 0.25f, height * 0.45f, width * 0.5f, height * 0.52f, width * 0.75f, height * 0.42f)
        lineTo(width, height * 0.50f)
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    drawPath(path = hillPath, color = Color(0xFFA5D6A7).copy(alpha = 0.65f))

    // 3. Foreground Wheat Field
    val fieldColor = when (stage) {
        0 -> Color(0xFF8D6E63) // Prepared soil
        1 -> Color(0xFF66BB6A) // Young green shoots
        2 -> Color(0xFF43A047) // Dense green tillers
        3 -> Color(0xFFC0CA33) // Grain filling golden-green
        else -> Color(0xFFFFA000) // Ripe golden harvest
    }

    val foregroundPath = Path().apply {
        moveTo(0f, height * 0.62f)
        cubicTo(width * 0.3f, height * 0.56f, width * 0.7f, height * 0.65f, width, height * 0.58f)
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    drawPath(path = foregroundPath, color = fieldColor)

    // Wheat texture / stalks
    val stalkColor = if (stage >= 3) Color(0xFFFFD54F) else Color(0xFF81C784)
    for (i in 0..18) {
        val x = (width * 0.05f) + (i * width * 0.05f)
        val yBase = height * 0.75f + (i % 3) * 10f
        drawLine(
            color = stalkColor,
            start = Offset(x, yBase),
            end = Offset(x + 4f, yBase - 22.dp.toPx()),
            strokeWidth = 3.dp.toPx()
        )
        // Ear head
        drawCircle(
            color = if (stage >= 3) Color(0xFFFFB300) else Color(0xFF4CAF50),
            radius = 3.dp.toPx(),
            center = Offset(x + 4f, yBase - 24.dp.toPx())
        )
    }

    // 4. Farmhouse (Stage 0+)
    val houseLeft = width * 0.12f
    val houseTop = height * 0.48f
    drawRect(
        color = Color(0xFFBCAAA4),
        topLeft = Offset(houseLeft, houseTop),
        size = Size(46.dp.toPx(), 32.dp.toPx())
    )
    // House Roof
    val roofPath = Path().apply {
        moveTo(houseLeft - 6.dp.toPx(), houseTop)
        lineTo(houseLeft + 23.dp.toPx(), houseTop - 18.dp.toPx())
        lineTo(houseLeft + 52.dp.toPx(), houseTop)
        close()
    }
    drawPath(path = roofPath, color = Color(0xFF8D6E63))
    // Door
    drawRect(
        color = Color(0xFF5D4037),
        topLeft = Offset(houseLeft + 16.dp.toPx(), houseTop + 14.dp.toPx()),
        size = Size(14.dp.toPx(), 18.dp.toPx())
    )

    // 5. Stage 1+ : Cow / Livestock
    if (stage >= 1) {
        val cowX = width * 0.45f
        val cowY = height * 0.62f
        // Body
        drawOval(
            color = Color(0xFFEEEEEE),
            topLeft = Offset(cowX, cowY),
            size = Size(26.dp.toPx(), 16.dp.toPx())
        )
        // Black patches
        drawCircle(color = Color(0xFF333333), radius = 4.dp.toPx(), center = Offset(cowX + 8.dp.toPx(), cowY + 6.dp.toPx()))
        // Head
        drawCircle(color = Color(0xFFEEEEEE), radius = 6.dp.toPx(), center = Offset(cowX + 26.dp.toPx(), cowY + 4.dp.toPx()))
        // Legs
        drawLine(color = Color(0xFF333333), start = Offset(cowX + 4.dp.toPx(), cowY + 14.dp.toPx()), end = Offset(cowX + 4.dp.toPx(), cowY + 24.dp.toPx()), strokeWidth = 2.dp.toPx())
        drawLine(color = Color(0xFF333333), start = Offset(cowX + 20.dp.toPx(), cowY + 14.dp.toPx()), end = Offset(cowX + 20.dp.toPx(), cowY + 24.dp.toPx()), strokeWidth = 2.dp.toPx())
    }

    // 6. Stage 2+ : Water Storage Tank / Pond (تالاب)
    if (stage >= 2) {
        val tankX = width * 0.65f
        val tankY = height * 0.58f
        drawOval(
            color = Color(0xFF0288D1),
            topLeft = Offset(tankX, tankY),
            size = Size(36.dp.toPx(), 18.dp.toPx())
        )
        drawOval(
            color = Color(0xFFB0BEC5),
            topLeft = Offset(tankX - 2.dp.toPx(), tankY - 2.dp.toPx()),
            size = Size(40.dp.toPx(), 22.dp.toPx()),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
        )
    }

    // 7. Stage 4 : Red Massey Ferguson Tractor in courtyard
    if (stage >= 4) {
        val trX = width * 0.72f
        val trY = height * 0.68f
        // Big Rear Wheel
        drawCircle(color = Color(0xFF212121), radius = 10.dp.toPx(), center = Offset(trX + 6.dp.toPx(), trY + 12.dp.toPx()))
        drawCircle(color = Color(0xFFBDBDBD), radius = 5.dp.toPx(), center = Offset(trX + 6.dp.toPx(), trY + 12.dp.toPx()))
        // Small Front Wheel
        drawCircle(color = Color(0xFF212121), radius = 6.dp.toPx(), center = Offset(trX + 32.dp.toPx(), trY + 16.dp.toPx()))
        // Red Hood & Body
        drawRect(
            color = Color(0xFFD32F2F),
            topLeft = Offset(trX + 10.dp.toPx(), trY),
            size = Size(24.dp.toPx(), 12.dp.toPx())
        )
        // Cabin frame
        drawLine(color = Color.Black, start = Offset(trX + 6.dp.toPx(), trY), end = Offset(trX + 6.dp.toPx(), trY - 14.dp.toPx()), strokeWidth = 2.dp.toPx())
        drawLine(color = Color.Black, start = Offset(trX + 18.dp.toPx(), trY), end = Offset(trX + 18.dp.toPx(), trY - 14.dp.toPx()), strokeWidth = 2.dp.toPx())
        drawLine(color = Color(0xFFD32F2F), start = Offset(trX + 4.dp.toPx(), trY - 14.dp.toPx()), end = Offset(trX + 20.dp.toPx(), trY - 14.dp.toPx()), strokeWidth = 3.dp.toPx())
    }
}
