package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.KisanAiService
import com.example.data.repository.KisanRepository
import com.example.ui.components.PartnerDashboardDialog
import com.example.ui.screens.AskScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.FieldHealthScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.SummerPlanScreen
import com.example.ui.theme.BarkatGold
import com.example.ui.theme.FieldGreen
import com.example.ui.theme.KisanSaathiTheme
import com.example.ui.theme.NotoNastaliqUrdu
import com.example.util.AppLanguage
import com.example.util.AudioHelper
import com.example.util.LocalAppLanguage
import com.example.util.configureAppCheck
import com.example.util.isUrdu
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private lateinit var audioHelper: AudioHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check contract wiring before UI or ViewModels
        configureAppCheck(this, intent)

        audioHelper = AudioHelper(this)
        enableEdgeToEdge()

        setContent {
            KisanSaathiTheme {
                MainAppHost(audioHelper = audioHelper)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioHelper.shutdown()
    }
}

enum class NavigationTab(val titleUr: String, val titleEn: String) {
    HOME("روڈ میپ", "Roadmap"),
    HEALTH("صحت کھیت", "Field Health"),
    ASK("پوچھیں", "Ask Voice"),
    SUMMER("گرمیوں کا انتخاب", "Summer Plan"),
    REWARDS("انعامات", "Rewards")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppHost(audioHelper: AudioHelper) {
    val context = LocalContext.current
    val repository = remember { KisanRepository(context) }
    val aiService = remember { KisanAiService() }

    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isDemoModeActive by remember { mutableStateOf(false) }
    var appLanguage by remember { mutableStateOf(AppLanguage.URDU) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    CompositionLocalProvider(LocalAppLanguage provides appLanguage) {
        // Auth Gating: If not logged in and not in demo mode, display AuthScreen
        if (currentUser == null && !isDemoModeActive) {
            AuthScreen(
                onAuthSuccess = {
                    currentUser = Firebase.auth.currentUser
                },
                onContinueAsDemoFarmer = {
                    isDemoModeActive = true
                },
                onToggleLanguage = {
                    appLanguage = if (appLanguage.isUrdu) AppLanguage.ENGLISH else AppLanguage.URDU
                }
            )
            return@CompositionLocalProvider
        }

        var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
        var showPartnerDashboard by remember { mutableStateOf(false) }
        var showHistoryScreen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            // Quick toggle or logo tap
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(FieldGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Agriculture,
                                contentDescription = "Logo",
                                tint = BarkatGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (appLanguage.isUrdu) "کسان ساتھی" else "Kisan Saathi",
                                fontFamily = if (appLanguage.isUrdu) NotoNastaliqUrdu else null,
                                fontSize = if (appLanguage.isUrdu) 21.sp else 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = FieldGreen
                            )
                            Text(
                                text = if (appLanguage.isUrdu) "Kisan Saathi · Pothohar" else "Rainfed Farm Companion · Pothohar",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Language Switcher Toggle Pill (Urdu <-> English)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                appLanguage = if (appLanguage.isUrdu) AppLanguage.ENGLISH else AppLanguage.URDU
                            }
                            .border(
                                width = 1.2.dp,
                                color = FieldGreen.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("language_toggle_btn"),
                        color = FieldGreen.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Switch language between Urdu and English",
                                tint = FieldGreen,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (appLanguage.isUrdu) "اردو" else "ENG",
                                fontFamily = if (appLanguage.isUrdu) NotoNastaliqUrdu else null,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (appLanguage.isUrdu) 13.sp else 11.sp,
                                color = FieldGreen
                            )
                            Text(
                                text = if (appLanguage.isUrdu) " | EN" else " | اردو",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Past Seasons History
                    IconButton(
                        onClick = { showHistoryScreen = !showHistoryScreen },
                        modifier = Modifier.testTag("history_nav_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Past Seasons",
                            tint = if (showHistoryScreen) FieldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Partner Extension View
                    IconButton(
                        onClick = { showPartnerDashboard = true },
                        modifier = Modifier.testTag("partner_view_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Partner Dashboard",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // Home (Roadmap)
                NavigationBarItem(
                    selected = selectedTab == NavigationTab.HOME && !showHistoryScreen,
                    onClick = {
                        showHistoryScreen = false
                        selectedTab = NavigationTab.HOME
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = {
                        Text(
                            text = if (appLanguage.isUrdu) NavigationTab.HOME.titleUr else NavigationTab.HOME.titleEn,
                            fontSize = if (appLanguage.isUrdu) 12.sp else 11.sp,
                            fontFamily = if (appLanguage.isUrdu) NotoNastaliqUrdu else null
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = FieldGreen, indicatorColor = FieldGreen.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("nav_home")
                )

                // Field Health
                NavigationBarItem(
                    selected = selectedTab == NavigationTab.HEALTH && !showHistoryScreen,
                    onClick = {
                        showHistoryScreen = false
                        selectedTab = NavigationTab.HEALTH
                    },
                    icon = { Icon(Icons.Default.Spa, contentDescription = "Health") },
                    label = {
                        Text(
                            text = if (appLanguage.isUrdu) NavigationTab.HEALTH.titleUr else NavigationTab.HEALTH.titleEn,
                            fontSize = if (appLanguage.isUrdu) 12.sp else 11.sp,
                            fontFamily = if (appLanguage.isUrdu) NotoNastaliqUrdu else null
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = FieldGreen, indicatorColor = FieldGreen.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("nav_health")
                )

                // Ask (Center Mic)
                NavigationBarItem(
                    selected = selectedTab == NavigationTab.ASK && !showHistoryScreen,
                    onClick = {
                        showHistoryScreen = false
                        selectedTab = NavigationTab.ASK
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FieldGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Ask", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    },
                    label = {
                        Text(
                            text = if (appLanguage.isUrdu) NavigationTab.ASK.titleUr else NavigationTab.ASK.titleEn,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FieldGreen,
                            fontFamily = if (appLanguage.isUrdu) NotoNastaliqUrdu else null
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = FieldGreen, indicatorColor = Color.Transparent),
                    modifier = Modifier.testTag("nav_ask")
                )

                // Summer Plan
                NavigationBarItem(
                    selected = selectedTab == NavigationTab.SUMMER && !showHistoryScreen,
                    onClick = {
                        showHistoryScreen = false
                        selectedTab = NavigationTab.SUMMER
                    },
                    icon = { Icon(Icons.Default.WbSunny, contentDescription = "Summer") },
                    label = {
                        Text(
                            text = if (appLanguage.isUrdu) NavigationTab.SUMMER.titleUr else NavigationTab.SUMMER.titleEn,
                            fontSize = if (appLanguage.isUrdu) 11.sp else 10.sp,
                            fontFamily = if (appLanguage.isUrdu) NotoNastaliqUrdu else null
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = FieldGreen, indicatorColor = FieldGreen.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("nav_summer")
                )

                // Rewards
                NavigationBarItem(
                    selected = selectedTab == NavigationTab.REWARDS && !showHistoryScreen,
                    onClick = {
                        showHistoryScreen = false
                        selectedTab = NavigationTab.REWARDS
                    },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Rewards") },
                    label = {
                        Text(
                            text = if (appLanguage.isUrdu) NavigationTab.REWARDS.titleUr else NavigationTab.REWARDS.titleEn,
                            fontSize = if (appLanguage.isUrdu) 12.sp else 11.sp,
                            fontFamily = if (appLanguage.isUrdu) NotoNastaliqUrdu else null
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = FieldGreen, indicatorColor = FieldGreen.copy(alpha = 0.15f)),
                    modifier = Modifier.testTag("nav_rewards")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showHistoryScreen) {
                HistoryScreen(repository = repository)
            } else {
                when (selectedTab) {
                    NavigationTab.HOME -> HomeScreen(
                        repository = repository,
                        audioHelper = audioHelper,
                        onNavigateToHistory = { showHistoryScreen = true }
                    )
                    NavigationTab.HEALTH -> FieldHealthScreen(repository = repository)
                    NavigationTab.ASK -> AskScreen(aiService = aiService, audioHelper = audioHelper)
                    NavigationTab.SUMMER -> SummerPlanScreen(repository = repository)
                    NavigationTab.REWARDS -> RewardsScreen(repository = repository)
                }
            }
        }
    }

    if (showPartnerDashboard) {
        PartnerDashboardDialog(
            onDismiss = { showPartnerDashboard = false }
        )
    }
    }
}
