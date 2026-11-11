package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeItem
import com.example.data.repository.KisanRepository
import com.example.ui.components.BarkatBasisDialog
import com.example.ui.theme.BarkatGold
import com.example.ui.theme.FieldGreen
import com.example.ui.theme.NotoNastaliqUrdu
import com.example.ui.theme.WheatGold
import com.example.util.LocalAppLanguage
import com.example.util.isUrdu

@Composable
fun RewardsScreen(
    repository: KisanRepository,
    modifier: Modifier = Modifier
) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    val profile = remember { repository.getDemoFarmerProfile() }
    val badges = remember { repository.getBadges() }
    val leaderboard = remember { repository.getVillageLeaderboard() }

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Village, 1: Tehsil Teams
    var showBarkatDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("rewards_screen")
    ) {
        // Screen Title
        Text(
            text = if (isUrdu) "انعامات اور برکت (Rewards & Community)" else "Rewards & Community (انعامات اور برکت)",
            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
            fontSize = if (isUrdu) 24.sp else 21.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isUrdu) "بروقت فیصلوں، مسلسل لگن اور خوراک کے ضیاع سے بچاؤ کی حوصلہ افزائی"
                else "Celebrating timely decisions, consistency streaks, and crop loss prevention",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Level & Points Card
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
                    Column {
                        Text(
                            text = if (isUrdu) "آپ کا درجہ: محنتی کسان (Mehnati Kisan)" else "Your Level: Diligent Farmer (محنتی کسان)",
                            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                            fontSize = if (isUrdu) 18.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FieldGreen
                        )
                        Text(
                            text = if (isUrdu) "اگلا درجہ: ہوشیار کسان (Hoshiyar Kisan)" else "Next: Expert Farmer (ہوشیار کسان)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Streak Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF3E0),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BarkatGold.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isUrdu) "${profile.streak_weeks} ہفتے مسلسل" else "${profile.streak_weeks} Wks Streak",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress to next level
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isUrdu) "پوائنٹس: ${profile.points} / ۵۰۰" else "Points: ${profile.points} / 500",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isUrdu) "۶۸٪ مکمل" else "68% Complete",
                        fontSize = 12.sp,
                        color = FieldGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { 0.68f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = FieldGreen,
                    trackColor = FieldGreen.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Maunds saved clickable
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showBarkatDialog = true }
                        .background(BarkatGold.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUrdu) "بچائی گئی مجموعی برکت: ${profile.barkat_total_maund} من"
                                else "Total Barkat Saved: ${profile.barkat_total_maund} maunds",
                            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isUrdu) 15.sp else 13.sp,
                            color = FieldGreen
                        )
                        Text(
                            text = if (isUrdu) "اندازہ (تفصیل) >" else "Basis (Details) >",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WheatGold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Badges Section
        Text(
            text = if (isUrdu) "زرعی تمغے (Earned Badges):" else "Agricultural Badges (زرعی تمغے):",
            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Badges Grid
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            badges.chunked(2).forEach { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowBadges.forEach { badge ->
                        BadgeCard(
                            badge = badge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowBadges.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Community Leaderboards Section
        Text(
            text = if (isUrdu) "کمیونٹی لیڈر بورڈ (Village Leaderboard):" else "Community Leaderboard (لیڈر بورڈ):",
            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isUrdu) "رینکنگ صرف بروقت فیصلوں اور اسٹریک پر مبنی ہے — کسی کی ذاتی آمدنی ظاہر نہیں ہوتی۔"
                else "Rankings reflect timely actions & consistency — private farm finances are never revealed.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text(if (isUrdu) "گاؤں ڈھڈیال (Village)" else "Dhudial Village") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text(if (isUrdu) "تحصیل مقابلے (Chakwal Teams)" else "Chakwal Teams") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTabIndex == 0) {
            // Village Farmer List
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    leaderboard.forEach { farmer ->
                        LeaderboardRow(farmer = farmer, isCurrentUser = farmer.id == "farmer_2")
                    }
                }
            }
        } else {
            // Village-vs-Village Team Board
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TeamRankRow(
                        rank = 1,
                        villageName = if (isUrdu) "ڈھڈیال (Dhudial)" else "Dhudial",
                        totalMaundsSaved = 142.5,
                        timelyScore = 890
                    )
                    TeamRankRow(
                        rank = 2,
                        villageName = if (isUrdu) "بھون (Bhaun)" else "Bhaun",
                        totalMaundsSaved = 128.0,
                        timelyScore = 810
                    )
                    TeamRankRow(
                        rank = 3,
                        villageName = if (isUrdu) "کلر کہار (Kallar Kahar)" else "Kallar Kahar",
                        totalMaundsSaved = 110.0,
                        timelyScore = 740
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showBarkatDialog) {
        BarkatBasisDialog(
            maundEstimate = profile.barkat_total_maund,
            lossFactorBasis = "مجموعی برکت: سیزن کے دوران بروقت بوائی، یوریا کھاد بچاؤ اور کٹائی اقدامات سے بچائی گئی گندم کا باضابطہ اندراج",
            onDismiss = { showBarkatDialog = false }
        )
    }
}

@Composable
fun BadgeCard(badge: BadgeItem, modifier: Modifier = Modifier) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isEarned) Color(0xFFF1F8E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (badge.isEarned) FieldGreen.copy(alpha = 0.4f) else Color.Transparent
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getBadgeIcon(badge.icon),
                    contentDescription = null,
                    tint = if (badge.isEarned) FieldGreen else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                if (badge.isEarned) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = FieldGreen, modifier = Modifier.size(16.dp))
                } else {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isUrdu) badge.titleUr else badge.titleEn,
                fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                fontWeight = FontWeight.Bold,
                fontSize = if (isUrdu) 15.sp else 13.sp,
                color = if (badge.isEarned) MaterialTheme.colorScheme.onSurface else Color.Gray
            )

            Text(
                text = badge.descriptionUr,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = if (badge.isEarned) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray
            )

            badge.earnedDate?.let { date ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isUrdu) "حاصل کیا: $date" else "Earned: $date",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = FieldGreen
                )
            }
        }
    }
}

@Composable
private fun LeaderboardRow(farmer: com.example.data.model.VillageLeaderboardFarmer, isCurrentUser: Boolean) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (isCurrentUser) Color(0xFFE8F5E9) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(if (farmer.rank <= 3) BarkatGold else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${farmer.rank}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = farmer.nickname,
                        fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isCurrentUser) FieldGreen else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isUrdu) "اسٹریک: ${farmer.streakWeeks} ہفتے · برکت: ${farmer.barkatTotalMaund} من"
                            else "Streak: ${farmer.streakWeeks} wks · Barkat: ${farmer.barkatTotalMaund} maunds",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "${farmer.score} pts",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isCurrentUser) FieldGreen else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TeamRankRow(rank: Int, villageName: String, totalMaundsSaved: Double, timelyScore: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "#$rank", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (rank == 1) BarkatGold else Color.Gray)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = villageName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "مجموعی محفوظ اناج: $totalMaundsSaved من", fontSize = 12.sp, color = FieldGreen)
            }
        }
        Text(text = "$timelyScore pts", fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

private fun getBadgeIcon(iconName: String): ImageVector {
    return when (iconName) {
        "water_drop" -> Icons.Default.WaterDrop
        "umbrella" -> Icons.Default.Umbrella
        "eco" -> Icons.Default.Eco
        "sparkles" -> Icons.Default.AutoAwesome
        "menu_book" -> Icons.Default.MenuBook
        "wb_sunny" -> Icons.Default.WbSunny
        "group" -> Icons.Default.Group
        else -> Icons.Default.EmojiEvents
    }
}
