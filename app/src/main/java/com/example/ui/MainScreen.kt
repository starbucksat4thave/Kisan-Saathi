package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.data.model.GeminiModelTier
import com.example.data.repository.AiRepository
import com.example.data.repository.JournalRepository
import com.example.data.repository.PlacesRepository
import com.example.ui.components.WanderPulseTopBar
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.journal.JournalScreen
import com.example.ui.screens.studio.StudioScreen
import com.example.ui.screens.transcribe.TranscribeScreen
import com.example.ui.screens.voice.VoiceScreen

enum class NavDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    EXPLORE("explore", "Explore", Icons.Filled.Explore, Icons.Outlined.Explore),
    VOICE("voice", "Voice Live", Icons.Filled.RecordVoiceOver, Icons.Outlined.RecordVoiceOver),
    TRANSCRIBE("transcribe", "Transcribe", Icons.Filled.Mic, Icons.Outlined.Mic),
    STUDIO("studio", "Studio", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    JOURNAL("journal", "Journal", Icons.Filled.Book, Icons.Outlined.Book)
}

@Composable
fun MainScreen(
    placesRepository: PlacesRepository = remember { PlacesRepository() },
    aiRepository: AiRepository = remember { com.example.data.repository.PlaceholderAiRepository() },
    journalRepository: JournalRepository = remember(placesRepository) { JournalRepository(placesRepository) }
) {
    var currentDestination by remember { mutableStateOf(NavDestination.EXPLORE) }
    var currentModel by remember { mutableStateOf(GeminiModelTier.FLASH) }

    // Android back handling
    BackHandler(enabled = currentDestination != NavDestination.EXPLORE) {
        currentDestination = NavDestination.EXPLORE
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            WanderPulseTopBar(
                currentModel = currentModel,
                onModelSelected = { currentModel = it },
                onOpenDeployGuide = { currentDestination = NavDestination.JOURNAL }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("wanderpulse_bottom_nav")
            ) {
                NavDestination.values().forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.title
                            )
                        },
                        label = { Text(destination.title) },
                        modifier = Modifier.testTag("nav_${destination.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                NavDestination.EXPLORE -> {
                    ExploreScreen(
                        placesRepository = placesRepository,
                        journalRepository = journalRepository,
                        aiRepository = aiRepository
                    )
                }
                NavDestination.VOICE -> {
                    VoiceScreen(
                        aiRepository = aiRepository
                    )
                }
                NavDestination.TRANSCRIBE -> {
                    TranscribeScreen(
                        aiRepository = aiRepository,
                        journalRepository = journalRepository
                    )
                }
                NavDestination.STUDIO -> {
                    StudioScreen(
                        aiRepository = aiRepository,
                        journalRepository = journalRepository
                    )
                }
                NavDestination.JOURNAL -> {
                    JournalScreen(
                        journalRepository = journalRepository
                    )
                }
            }
        }
    }
}
