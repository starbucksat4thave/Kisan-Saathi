package com.example.ui.screens

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SummerPlanOption
import com.example.data.repository.KisanRepository
import com.example.ui.theme.BarkatGold
import com.example.ui.theme.FieldGreen
import com.example.ui.theme.NotoNastaliqUrdu
import com.example.ui.theme.WheatGold
import com.example.util.LocalAppLanguage
import com.example.util.isUrdu

@Composable
fun SummerPlanScreen(
    repository: KisanRepository,
    modifier: Modifier = Modifier
) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    var incomeWeight by remember { mutableFloatStateOf(0.7f) }
    var waterWeight by remember { mutableFloatStateOf(0.5f) }
    var soilWeight by remember { mutableFloatStateOf(0.6f) }
    var cattleWeight by remember { mutableFloatStateOf(0.4f) }

    val rankedOptions = remember(incomeWeight, waterWeight, soilWeight, cattleWeight) {
        repository.getSummerRotationOptions(
            waterWeight = waterWeight,
            incomeWeight = incomeWeight,
            soilWeight = soilWeight,
            cattleFeedWeight = cattleWeight
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("summer_plan_screen")
    ) {
        // Title
        Text(
            text = if (isUrdu) "گرمیوں کی فصل کا انتخاب (Summer Plan)" else "Summer Crop Rotation Planner (گرمیوں کا انتخاب)",
            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
            fontSize = if (isUrdu) 24.sp else 21.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isUrdu) "ناسا POWER موسمیاتی ۲۰ سالہ تاریخ (2005–2025) اور AquaCrop ماڈل پر مبنی تقابل"
                else "Based on NASA POWER 20-year climatology (2005-2025) & AquaCrop modeling",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Sliders Card (Farmer Priorities)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = FieldGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "آپ کی ترجیحات کے مطابق رینکنگ ایڈجسٹ کریں:" else "Adjust ranking by your farm priorities:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Income slider
                PrioritySliderItem(
                    label = if (isUrdu) "آمدنی اور منافع (Income)" else "Net Income & Profit",
                    value = incomeWeight,
                    color = FieldGreen,
                    onValueChange = { incomeWeight = it }
                )

                // Water saving slider
                PrioritySliderItem(
                    label = if (isUrdu) "پانی کی بچت (Water Saving)" else "Water Conservation",
                    value = waterWeight,
                    color = Color(0xFF0288D1),
                    onValueChange = { waterWeight = it }
                )

                // Soil health slider
                PrioritySliderItem(
                    label = if (isUrdu) "زمین کی زرخیزی (Soil Health)" else "Soil Organic Health & Cover",
                    value = soilWeight,
                    color = Color(0xFF6D4C41),
                    onValueChange = { soilWeight = it }
                )

                // Cattle feed slider
                PrioritySliderItem(
                    label = if (isUrdu) "مویشیوں کا چارہ (Cattle Feed)" else "Cattle Fodder Value",
                    value = cattleWeight,
                    color = WheatGold,
                    onValueChange = { cattleWeight = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isUrdu) "تجویز کردہ درجہ بندی (Ranked Options):" else "Recommended Rotation Ranking:",
            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Options Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            rankedOptions.forEachIndexed { rankIndex, option ->
                SummerOptionCard(
                    rank = rankIndex + 1,
                    option = option
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PrioritySliderItem(
    label: String,
    value: Float,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = "${(value * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(alpha = 0.2f)
            ),
            modifier = Modifier.height(30.dp)
        )
    }
}

@Composable
fun SummerOptionCard(
    rank: Int,
    option: SummerPlanOption
) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    val isTopPick = rank == 1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (isTopPick) 2.dp else 1.dp,
                color = if (isTopPick) FieldGreen else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isTopPick) Color(0xFFF1F8E9) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTopPick) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Rank and Title Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (isTopPick) FieldGreen else Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#$rank",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isUrdu) option.nameUr else option.nameEn,
                            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                            fontSize = if (isUrdu) 18.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isUrdu) option.nameEn else option.nameUr,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isTopPick) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FieldGreen
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = BarkatGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isUrdu) "بہترین انتخاب" else "Top Pick",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = option.descriptionUr,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 20-Year AquaCrop Replay Badge
            if (option.winYearsAgainstFallowPct > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Spa, contentDescription = null, tint = FieldGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isUrdu) "۲۰ سالہ تاریخی ریکارڈ: ۲۰ میں سے ${option.winYearsAgainstFallowPct * 20 / 100} سال خالی کھیت سے زیادہ منافع!"
                                else "20-Year NASA Replay: Outperformed fallow in ${option.winYearsAgainstFallowPct * 20 / 100} of 20 years!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FieldGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Metrics comparison bars
            MetricBar(
                label = if (isUrdu) "خالص منافع" else "Net Margin",
                valueText = if (isUrdu) "Rs. ${option.netMarginPkr} فی ایکڑ" else "Rs. ${option.netMarginPkr} / acre",
                progress = option.netMarginPkr / 60000f,
                barColor = FieldGreen
            )
            MetricBar(
                label = if (isUrdu) "زمین کا ڈھانپ (کٹاؤ سے حفاظت)" else "Soil Cover (Anti-erosion)",
                valueText = "${option.soilCoverScore}%",
                progress = option.soilCoverScore / 100f,
                barColor = Color(0xFF6D4C41)
            )
            MetricBar(
                label = if (isUrdu) "پانی کا استعمال" else "Water Footprint",
                valueText = "${option.avgWaterUseMm.toInt()} mm",
                progress = (option.avgWaterUseMm / 250f).toFloat(),
                barColor = Color(0xFF0288D1)
            )
            MetricBar(
                label = if (isUrdu) "مویشیوں کے چارے کا اسکور" else "Cattle Fodder Score",
                valueText = "${option.cattleFeedScore}/100",
                progress = option.cattleFeedScore / 100f,
                barColor = WheatGold
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isUrdu) "بارانی کٹاؤ کا خطرہ: ${option.erosionRisk} · پیداوار: ${option.avgYieldText}"
                    else "Erosion Risk: ${option.erosionRisk} · Avg Yield: ${option.avgYieldText}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MetricBar(label: String, valueText: String, progress: Float, barColor: Color) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = valueText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = barColor,
            trackColor = barColor.copy(alpha = 0.15f)
        )
    }
}
