package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdvisoryCard
import com.example.data.model.AdvisorySeverity
import com.example.ui.theme.BarkatGold
import com.example.ui.theme.FieldEmerald
import com.example.ui.theme.FieldGreen
import com.example.ui.theme.NotoNastaliqUrdu
import com.example.ui.theme.WheatGold
import com.example.util.LocalAppLanguage
import com.example.util.isUrdu

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdvisoryCardSheet(
    advisory: AdvisoryCard,
    onPlayAudio: () -> Unit,
    onMarkDone: () -> Unit,
    onTapBarkatEstimate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = LocalAppLanguage.current.isUrdu

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(
                width = 1.5.dp,
                color = when (advisory.severity) {
                    AdvisorySeverity.ALERT -> Color(0xFFE57373)
                    AdvisorySeverity.ACT -> FieldEmerald
                    AdvisorySeverity.INFO -> Color(0xFF64B5F6)
                },
                shape = RoundedCornerShape(22.dp)
            )
            .testTag("advisory_card_${advisory.key}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Severity and Confidence Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Severity Tag
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (advisory.severity) {
                                AdvisorySeverity.ALERT -> Color(0xFFFFEBEE)
                                AdvisorySeverity.ACT -> Color(0xFFE8F5E9)
                                AdvisorySeverity.INFO -> Color(0xFFE3F2FD)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (advisory.severity) {
                            AdvisorySeverity.ALERT -> Icons.Default.Warning
                            AdvisorySeverity.ACT -> Icons.Default.CheckCircle
                            AdvisorySeverity.INFO -> Icons.Default.SatelliteAlt
                        },
                        contentDescription = null,
                        tint = when (advisory.severity) {
                            AdvisorySeverity.ALERT -> Color(0xFFD32F2F)
                            AdvisorySeverity.ACT -> FieldGreen
                            AdvisorySeverity.INFO -> Color(0xFF1976D2)
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (advisory.severity) {
                            AdvisorySeverity.ALERT -> if (isUrdu) "فوری توجہ (Alert)" else "Alert Window"
                            AdvisorySeverity.ACT -> if (isUrdu) "عملی اقدام (Action)" else "Recommended Action"
                            AdvisorySeverity.INFO -> if (isUrdu) "معلوماتی رہنمائی (Info)" else "Field Advisory"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (advisory.severity) {
                            AdvisorySeverity.ALERT -> Color(0xFFD32F2F)
                            AdvisorySeverity.ACT -> FieldGreen
                            AdvisorySeverity.INFO -> Color(0xFF1976D2)
                        }
                    )
                }

                // Confidence chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (isUrdu) "اعتماد: زیادہ (High)" else "Confidence: High",
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Title
            Text(
                text = if (isUrdu) advisory.titleUr else advisory.titleEn,
                fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                fontSize = if (isUrdu) 22.sp else 20.sp,
                lineHeight = if (isUrdu) 36.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Subtitle
            Text(
                text = if (isUrdu) advisory.titleEn else advisory.titleUr,
                fontFamily = if (!isUrdu) NotoNastaliqUrdu else null,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Body text
            Text(
                text = if (isUrdu) advisory.bodyUr else advisory.bodyEn,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // NASA Earth Observation Telemetry Strip
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // SMAP Moisture
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Opacity, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isUrdu) "مٹی نمی: ${(advisory.smapMoisture * 100).toInt()}%" else "Soil Moisture: ${(advisory.smapMoisture * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    // GDD
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Thermostat, contentDescription = null, tint = WheatGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "GDD: ${advisory.gddValue.toInt()}°C", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    // IMERG / Forecast Rain
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Thunderstorm, contentDescription = null, tint = Color(0xFF5C6BC0), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isUrdu) "بارش: ${advisory.rainForecastMm}mm" else "Rain: ${advisory.rainForecastMm}mm", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Memory reference from past seasons ("پچھلی بار...")
            advisory.memoryRef?.let { mem ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF8E1),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BarkatGold.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Past Season Memory",
                            tint = WheatGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isUrdu) "یاد دہانی (${mem.season})" else "Memory Alert (${mem.season})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF795548)
                            )
                            Text(
                                text = if (isUrdu) mem.textUr else mem.textEn,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = Color(0xFF4E342E)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // NASA Source Chips
            Text(
                text = if (isUrdu) "ڈیٹا ماخذ (NASA Data Sources):" else "NASA Earth Observation Sources:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                advisory.sources.forEach { sourceTag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FieldGreen.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = sourceTag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = FieldGreen,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Barkat-if-done chip (اندازہ)
            advisory.barkatIfDone?.let { barkat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BarkatGold.copy(alpha = 0.15f))
                        .clickable { onTapBarkatEstimate() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isUrdu) "برکت کا اندازہ: " else "Barkat Estimate: ",
                            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isUrdu) "+${barkat.maundEstimate} من متوقع بچت" else "+${barkat.maundEstimate} maunds saved",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = FieldGreen
                        )
                    }
                    Text(
                        text = if (isUrdu) "تفصیل دیکھیں >" else "View Details >",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WheatGold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Action Buttons: Listen & Done
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onPlayAudio() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("listen_audio_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FieldGreen)
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Listen")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUrdu) "آواز سنیں" else "Listen Guide",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onMarkDone() },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("mark_done_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FieldGreen)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Done")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUrdu) "مکمل کیا (Done)" else "Mark as Done",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
