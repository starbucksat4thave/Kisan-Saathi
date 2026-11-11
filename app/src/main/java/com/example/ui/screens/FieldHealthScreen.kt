package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.KisanRepository
import com.example.ui.theme.BarkatGold
import com.example.ui.theme.FieldEmerald
import com.example.ui.theme.FieldGreen
import com.example.ui.theme.NotoNastaliqUrdu
import com.example.ui.theme.WheatGold
import com.example.util.LocalAppLanguage
import com.example.util.isUrdu

@Composable
fun FieldHealthScreen(
    repository: KisanRepository,
    modifier: Modifier = Modifier
) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    val ndviPoints = remember { repository.getNdviCurve() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("field_health_screen")
    ) {
        // Title
        Text(
            text = if (isUrdu) "کھیت کی صحت (Satellite Field Health)" else "Satellite Field Health (کھیت کی صحت)",
            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
            fontSize = if (isUrdu) 24.sp else 21.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isUrdu) "ناسا HLS (30m) اور Sentinel-2 سیٹلائٹ سے حاصل کردہ پودوں کی ہریالی (NDVI)"
                else "Vegetation vigor (NDVI) derived from NASA HLS (30m) & Sentinel-2 satellites",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Status Card in Plain Language
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFE8F5E9),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FieldGreen.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = FieldGreen,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isUrdu) "فصل کی حالت: تسلی بخش اور مضبوط" else "Crop Condition: Satisfactory & Strong",
                        fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                        fontSize = if (isUrdu) 17.sp else 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FieldGreen
                    )
                    Text(
                        text = if (isUrdu) "آپ کی گندم کی نشوونما پچھلے کامیاب ترین سال (۲۰۲۴) جیسی شاندار ہے، اور ڈھڈیال کے اوسط کھیتوں سے بہتر ہے۔"
                            else "Your wheat growth closely tracks your best year (2024-25), performing above Dhudial regional average.",
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFF1B5E20)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Interactive NDVI Graph Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUrdu) "ہریالی کا تقابلی چارٹ (NDVI Curve)" else "Comparative Greenness (NDVI Curve)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "HLS 30m Resolution",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Canvas Graph
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val paddingX = 40f
                        val paddingY = 20f
                        val graphW = w - (paddingX * 2)
                        val graphH = h - (paddingY * 2)

                        // Draw Grid lines
                        for (i in 0..4) {
                            val y = paddingY + (graphH * (i / 4f))
                            drawLine(
                                color = Color.LightGray.copy(alpha = 0.4f),
                                start = Offset(paddingX, y),
                                end = Offset(w - paddingX, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        val n = ndviPoints.size
                        fun getX(idx: Int) = paddingX + (graphW * (idx.toFloat() / (n - 1)))
                        fun getY(valNdvi: Float) = paddingY + graphH * (1f - (valNdvi / 1.0f))

                        // Path 1: Regional Median (Dashed Gray)
                        val medianPath = Path()
                        ndviPoints.forEachIndexed { i, p ->
                            val x = getX(i)
                            val y = getY(p.regionalMedian)
                            if (i == 0) medianPath.moveTo(x, y) else medianPath.lineTo(x, y)
                        }
                        drawPath(
                            path = medianPath,
                            color = Color.Gray,
                            style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                        )

                        // Path 2: 2023-24 Late Season (Amber/Brown)
                        val path2023 = Path()
                        ndviPoints.forEachIndexed { i, p ->
                            val x = getX(i)
                            val y = getY(p.season2023)
                            if (i == 0) path2023.moveTo(x, y) else path2023.lineTo(x, y)
                        }
                        drawPath(path = path2023, color = Color(0xFFD32F2F).copy(alpha = 0.6f), style = Stroke(width = 2.dp.toPx()))

                        // Path 3: 2024-25 Ideal Season (Wheat Gold)
                        val path2024 = Path()
                        ndviPoints.forEachIndexed { i, p ->
                            val x = getX(i)
                            val y = getY(p.season2024)
                            if (i == 0) path2024.moveTo(x, y) else path2024.lineTo(x, y)
                        }
                        drawPath(path = path2024, color = WheatGold, style = Stroke(width = 2.dp.toPx()))

                        // Path 4: Current Season (Thick Vibrant Green)
                        val currentPath = Path()
                        ndviPoints.forEachIndexed { i, p ->
                            val x = getX(i)
                            val y = getY(p.currentSeason)
                            if (i == 0) currentPath.moveTo(x, y) else currentPath.lineTo(x, y)
                        }
                        drawPath(path = currentPath, color = FieldGreen, style = Stroke(width = 4.dp.toPx()))

                        // Current points dots
                        ndviPoints.forEachIndexed { i, p ->
                            drawCircle(color = FieldGreen, radius = 5.dp.toPx(), center = Offset(getX(i), getY(p.currentSeason)))
                            drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(getX(i), getY(p.currentSeason)))
                        }
                    }
                }

                // X-Axis Stage Labels
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ndviPoints.forEach { pt ->
                        Text(
                            text = if (isUrdu) pt.stageUr else pt.stageEn,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Legend
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    LegendItem(
                        color = FieldGreen,
                        label = if (isUrdu) "موجودہ سیزن ۲۰۲۶–۲۷ (Current Season)" else "Current Season 2026-27 (Akbar-19)"
                    )
                    LegendItem(
                        color = WheatGold,
                        label = if (isUrdu) "سیزن ۲۰۲۴–۲۵ (کامیاب ترین ریکارڈ ۳۶ من)" else "2024-25 Season (Record 36 maunds/acre)"
                    )
                    LegendItem(
                        color = Color(0xFFD32F2F),
                        label = if (isUrdu) "سیزن ۲۰۲۳–۲۴ (تاخیر سے بوائی ۱۸ من)" else "2023-24 Season (Late Sowing 18 maunds/acre)"
                    )
                    LegendItem(
                        color = Color.Gray,
                        label = if (isUrdu) "پوٹھوہار علاقائی اوسط (Regional Median)" else "Pothohar Regional Median (NASA HLS)"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Simple Field Zone Map (Bara Khet - 4 Acres)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = FieldGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "کھیت کا نقشہ (Bara Khet - 4 Acres Zones)" else "Field Zone Map (Bara Khet - 4 Acres)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Zone layout representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9))
                        .border(1.5.dp, FieldGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Zone A (North)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(Color(0xFF81C784).copy(alpha = 0.7f))
                                .border(1.dp, Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isUrdu) "شمالی حصہ (2 ایکڑ)" else "North Zone (2 Acres)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = if (isUrdu) "وتر: 26% (بہترین)" else "Moisture: 26% (Optimal)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                        // Zone B (South)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(Color(0xFFA5D6A7).copy(alpha = 0.5f))
                                .border(1.dp, Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isUrdu) "جنوبی ڈھلوان (2 ایکڑ)" else "South Slope (2 Acres)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = if (isUrdu) "وتر: 22% (مناسب)" else "Moisture: 22% (Adequate)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isUrdu) "مرکزی کوآرڈینیٹس: 32.93°N, 72.86°E · ڈھڈیال، چکوال · مٹی کی ساخت: لوم (Loam)"
                        else "Centroid: 32.93°N, 72.86°E · Dhudial, Chakwal · Soil Texture: Loam",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
