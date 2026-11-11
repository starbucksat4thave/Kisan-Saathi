package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HistoricalSeasonData
import com.example.data.repository.KisanRepository
import com.example.ui.theme.BarkatGold
import com.example.ui.theme.FieldGreen
import com.example.ui.theme.NotoNastaliqUrdu
import com.example.ui.theme.WheatGold
import com.example.util.LocalAppLanguage
import com.example.util.isUrdu

@Composable
fun HistoryScreen(
    repository: KisanRepository,
    modifier: Modifier = Modifier
) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    val seasons = remember { repository.getHistoricalSeasons() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("history_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.History, contentDescription = null, tint = FieldGreen, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (isUrdu) "ماضی کے سیزن اور سبق (Past Seasons)" else "Past Seasons & Lessons Learned (ماضی کے سیزن)",
                fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                fontSize = if (isUrdu) 24.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = if (isUrdu) "ناسا سیٹلائٹ واقعات، موسم کا اثر اور پچھلے سالوں کے تجربات"
                else "Historical weather impacts, NASA Earth events, and farmer lessons",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        seasons.forEach { season ->
            HistoricalSeasonCard(season = season)
            Spacer(modifier = Modifier.height(14.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun HistoricalSeasonCard(season: HistoricalSeasonData) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    val isGoodYear = season.yieldMaundPerAcre >= 30.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGoodYear) Color(0xFFF1F8E9) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isGoodYear) FieldGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Season Title & Yield
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isUrdu) season.seasonLabelUr else season.seasonLabelEn,
                        fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isUrdu) 18.sp else 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isUrdu) season.seasonLabelEn else season.seasonLabelUr,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isGoodYear) FieldGreen else Color(0xFF8D6E63)
                ) {
                    Text(
                        text = if (isUrdu) "${season.yieldMaundPerAcre.toInt()} من فی ایکڑ" else "${season.yieldMaundPerAcre.toInt()} maunds/acre",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dates Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = WheatGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isUrdu) "بوائی: ${season.sowingDate}" else "Sowing: ${season.sowingDate}", fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = FieldGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isUrdu) "کٹائی: ${season.harvestDate}" else "Harvest: ${season.harvestDate}", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // NASA Events attached to this season
            Text(
                text = if (isUrdu) "سیٹلائٹ واقعات (NASA Events):" else "NASA Observations & Events:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            season.eventsUr.forEach { event ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.SatelliteAlt, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = event, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lessons Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFF8E1),
                border = androidx.compose.foundation.BorderStroke(1.dp, WheatGold.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = WheatGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = season.lessonsUr,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF5D4037)
                    )
                }
            }

            season.badgeEarned?.let { badgeName ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = BarkatGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUrdu) "حاصل کردہ تمغہ: $badgeName" else "Badge Earned: $badgeName",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FieldGreen
                    )
                }
            }
        }
    }
}
