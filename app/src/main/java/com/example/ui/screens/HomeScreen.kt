package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdvisoryCard
import com.example.data.model.FarmerProfile
import com.example.data.model.RoadmapNode
import com.example.data.repository.KisanRepository
import com.example.ui.components.AdvisoryCardSheet
import com.example.ui.components.BarkatBasisDialog
import com.example.ui.components.BarkatSceneView
import com.example.ui.components.RoadmapView
import com.example.ui.theme.BarkatGold
import com.example.ui.theme.FieldGreen
import com.example.ui.theme.NotoNastaliqUrdu
import com.example.ui.theme.WheatGold
import com.example.util.AudioHelper
import com.example.util.LocalAppLanguage
import com.example.util.isUrdu
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    repository: KisanRepository,
    audioHelper: AudioHelper,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    val coroutineScope = rememberCoroutineScope()
    var farmerProfile by remember { mutableStateOf(repository.getDemoFarmerProfile()) }
    var simulatedDate by remember { mutableStateOf("2026-11-06") }
    var simulatedDateLabel by remember { mutableStateOf(if (isUrdu) "نومبر ۶ (بوائی)" else "Nov 6 (Sowing)") }

    var selectedNodeKey by remember { mutableStateOf("sowing") }
    var showBarkatDialog by remember { mutableStateOf(false) }
    var barkatBasisText by remember { mutableStateOf("") }
    var barkatMaundBasis by remember { mutableDoubleStateOf(1.5) }

    var showActionDoneDialog by remember { mutableStateOf(false) }
    var activeAdvisoryToComplete by remember { mutableStateOf<AdvisoryCard?>(null) }
    var showCelebrationBanner by remember { mutableStateOf(false) }

    val nodes = remember(simulatedDate) { repository.getRoadmapNodes(simulatedDate) }
    val advisories = remember(simulatedDate) { repository.getAdvisoriesForDate(simulatedDate) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // --- 1. Top Farmer Header & Barkat Chip ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isUrdu) "السلام علیکم، ${farmerProfile.nickname}!" else "Welcome, ${farmerProfile.name}!",
                    fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                    fontSize = if (isUrdu) 22.sp else 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isUrdu) "گاؤں ${farmerProfile.village} · بڑا کھیت (۴ ایکڑ بارانی)" else "Village ${farmerProfile.village} · Bara Khet (4-Acre Rainfed)",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Barkat Counter Pill
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        barkatBasisText = if (isUrdu) "مجموعی برکت: بوائی، کھاد اور کٹائی کے بروقت اقدامات سے بچائی گئی گندم" else "Total Barkat: Wheat protected from loss via timely sowing, fertilizer, and harvest"
                        barkatMaundBasis = farmerProfile.barkat_total_maund
                        showBarkatDialog = true
                    }
                    .testTag("barkat_counter_pill"),
                color = FieldGreen,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Barkat",
                        tint = BarkatGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUrdu) "برکت: %.1f من".format(farmerProfile.barkat_total_maund) else "Barkat: %.1f maund".format(farmerProfile.barkat_total_maund),
                        fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 2. Agro-weather Telemetry Strip ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Opacity, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isUrdu) "مٹی نمی: 24% (وتر ٹھیک)" else "Soil Moisture: 24% (Optimal)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Cloud, contentDescription = null, tint = Color(0xFF5C6BC0), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isUrdu) "پیش گوئی: 2 دن بعد بارش" else "Forecast: Rain in 2 days", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 3. Time-Travel Demonstration Bar (Simulate Crop Cycle) ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFFFF3E0),
            border = androidx.compose.foundation.BorderStroke(1.dp, WheatGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FastForward, contentDescription = null, tint = WheatGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isUrdu) "سیزن کا وقت تبدیل کریں (Time Travel Demo):" else "Simulation Date (Time Travel Demo):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF795548)
                        )
                    }
                    Text(
                        text = simulatedDateLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FieldGreen
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TimeTravelPill("نومبر (بوائی)", "2026-11-06", "نومبر ۶ (بوائی)", "sowing", simulatedDate) { d, l, n ->
                        simulatedDate = d; simulatedDateLabel = l; selectedNodeKey = n
                    }
                    TimeTravelPill("دسمبر (کھاد)", "2026-12-18", "دسمبر ۱۸ (یوریا)", "tillering_fertilizer", simulatedDate) { d, l, n ->
                        simulatedDate = d; simulatedDateLabel = l; selectedNodeKey = n
                    }
                    TimeTravelPill("جنوری (کورا)", "2027-01-15", "جنوری ۱۵ (کورا)", "frost_monitoring", simulatedDate) { d, l, n ->
                        simulatedDate = d; simulatedDateLabel = l; selectedNodeKey = n
                    }
                    TimeTravelPill("فروری (تیلہ)", "2027-02-15", "فروری ۱۵ (تیلہ)", "pest_scouting", simulatedDate) { d, l, n ->
                        simulatedDate = d; simulatedDateLabel = l; selectedNodeKey = n
                    }
                    TimeTravelPill("مارچ (گرمی)", "2027-03-22", "مارچ ۲۲ (گرمی)", "grain_filling_heat", simulatedDate) { d, l, n ->
                        simulatedDate = d; simulatedDateLabel = l; selectedNodeKey = n
                    }
                    TimeTravelPill("اپریل (کٹائی)", "2027-04-16", "اپریل ۱۶ (کٹائی)", "harvest", simulatedDate) { d, l, n ->
                        simulatedDate = d; simulatedDateLabel = l; selectedNodeKey = n
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 4. Celebration Banner if Action Completed ---
        AnimatedVisibility(
            visible = showCelebrationBanner,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp),
                color = FieldGreen,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Celebration, contentDescription = null, tint = BarkatGold, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isUrdu) "ماشاءاللہ! اقدام بروقت مکمل ہوا" else "Masha'Allah! Action completed on time",
                            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                            color = Color.White,
                            fontSize = if (isUrdu) 17.sp else 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isUrdu) "برکت میں اضافہ درج کر لیا گیا اور پوائنٹس مل گئے۔" else "Barkat recorded and points awarded.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // --- 5. Active Advisory Card Sheet for Current Stage ---
        advisories.firstOrNull()?.let { activeCard ->
            AdvisoryCardSheet(
                advisory = activeCard,
                onPlayAudio = {
                    audioHelper.speak(if (isUrdu) activeCard.bodyUr else activeCard.bodyEn)
                },
                onMarkDone = {
                    activeAdvisoryToComplete = activeCard
                    showActionDoneDialog = true
                },
                onTapBarkatEstimate = {
                    activeCard.barkatIfDone?.let { b ->
                        barkatBasisText = b.basis
                        barkatMaundBasis = b.maundEstimate
                        showBarkatDialog = true
                    }
                }
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // --- 6. Duolingo-style Seasonal Roadmap Path ---
        Text(
            text = if (isUrdu) "فصل کا روڈ میپ (Season Roadmap):" else "Crop Season Roadmap:",
            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isUrdu) "ہر مرحلے کے لیے ناسا سیٹلائٹ ڈیٹا کی بنیاد پر بروقت رہنمائی" else "Timely guidance for each stage powered by NASA Earth observations",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        RoadmapView(
            nodes = nodes,
            selectedNodeKey = selectedNodeKey,
            onSelectNode = { node ->
                selectedNodeKey = node.key
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // --- 7. Growing Barkat Harvest Scene (Scene Stage) ---
        Text(
            text = if (isUrdu) "آپ کی برکت کا منظر (Growing Harvest Scene):" else "Your Growing Harvest Scene (Barkat):",
            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isUrdu) "بروقت اقدامات سے محفوظ کی گئی گندم سے آپ کا گھرانہ اور کھیت خوشحال ہوتا ہے" else "Wheat saved through timely actions brings prosperity to your home and farm",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        BarkatSceneView(
            barkatMaunds = farmerProfile.barkat_total_maund,
            onTapDetails = {
                barkatBasisText = if (isUrdu) "برکت کا تخمینہ: فصل کے نقصان سے بچاؤ کا مجموعہ" else "Barkat estimate: Cumulative crop loss prevented across season"
                barkatMaundBasis = farmerProfile.barkat_total_maund
                showBarkatDialog = true
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Barkat Basis Explanation Dialog
    if (showBarkatDialog) {
        BarkatBasisDialog(
            maundEstimate = barkatMaundBasis,
            lossFactorBasis = barkatBasisText,
            onDismiss = { showBarkatDialog = false }
        )
    }

    // Action Confirmation Dialog
    if (showActionDoneDialog && activeAdvisoryToComplete != null) {
        val card = activeAdvisoryToComplete!!
        AlertDialog(
            onDismissRequest = { showActionDoneDialog = false },
            title = {
                Text(
                    text = if (isUrdu) "کارروائی کی تصدیق کریں" else "Confirm Action Completed",
                    fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isUrdu) "کیا آپ نے ${card.titleUr} پر عمل کر لیا ہے؟" else "Have you completed ${card.titleEn}?",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isUrdu) "اس اقدام سے اندازاً +${card.barkatIfDone?.maundEstimate ?: 1.5} من برکت اور +۲۰ پوائنٹس شامل ہوں گے۔"
                            else "This action adds approx +${card.barkatIfDone?.maundEstimate ?: 1.5} maunds barkat and +20 points.",
                        fontSize = 13.sp,
                        color = FieldGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showActionDoneDialog = false
                        coroutineScope.launch {
                            val saved = repository.markAdvisoryCompleted(card)
                            farmerProfile = farmerProfile.copy(
                                barkat_total_maund = farmerProfile.barkat_total_maund + saved,
                                points = farmerProfile.points + 20
                            )
                            showCelebrationBanner = true
                            audioHelper.speak(if (isUrdu) "مبارک ہو! اقدام درج ہو گیا۔ آپ کی برکت میں اضافہ ہوا۔" else "Congratulations! Action recorded. Your barkat has grown.")
                            delay(4000)
                            showCelebrationBanner = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FieldGreen),
                    modifier = Modifier.testTag("confirm_action_done_btn")
                ) {
                    Text(if (isUrdu) "ہاں، مکمل کر لیا" else "Yes, Completed")
                }
            },
            dismissButton = {
                TextButton(onClick = { showActionDoneDialog = false }) {
                    Text(if (isUrdu) "ابھی نہیں" else "Not Yet")
                }
            }
        )
    }
}

@Composable
private fun TimeTravelPill(
    label: String,
    dateValue: String,
    dateLabel: String,
    nodeKey: String,
    currentSimulatedDate: String,
    onSelect: (String, String, String) -> Unit
) {
    val isSelected = currentSimulatedDate == dateValue
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSelect(dateValue, dateLabel, nodeKey) },
        color = if (isSelected) FieldGreen else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FieldGreen else Color.LightGray)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Color(0xFF424242),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
