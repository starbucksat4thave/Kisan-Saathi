package com.example.ui.screens.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.JournalRepository

@Composable
fun JournalScreen(
    journalRepository: JournalRepository
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Itinerary, 1 = Postcards, 2 = GitHub Deploy Guide

    val itinerary by journalRepository.itinerary.collectAsState()
    val postcards by journalRepository.postcards.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var copiedNotice by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("journal_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Itinerary (${itinerary.size})") },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Postcards (${postcards.size})") },
                icon = { Icon(Icons.Default.Image, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Deploy GitHub") },
                icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) }
            )
        }

        when (selectedTab) {
            0 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Expedition Schedule",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Custom schedule crafted from your map and search discoveries",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                Button(
                                    onClick = {
                                        val summary = journalRepository.exportTripSummary()
                                        clipboardManager.setText(AnnotatedString(summary))
                                        copiedNotice = true
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (copiedNotice) "Copied!" else "Export")
                                }
                            }
                        }
                    }

                    if (itinerary.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                            ) {
                                Text(
                                    text = "No scheduled places yet. Go to Explore Map and tap '+ Itinerary' on any destination to add it to your plan.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    } else {
                        items(itinerary, key = { it.id }) { item ->
                            ElevatedCard(
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "Day ${item.dayNumber} • ${item.timeSlot}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = item.place.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Outlined.Place,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = item.place.address,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        if (item.customNotes.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Notes: ${item.customNotes}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(onClick = { journalRepository.removeFromItinerary(item.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (postcards.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                            ) {
                                Text(
                                    text = "No saved postcards yet. Head over to the Studio tab to generate 1K, 2K, or 4K AI travel artwork with gemini-3-pro-image-preview and save them here.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    } else {
                        items(postcards, key = { it.id }) { art ->
                            ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color(art.primaryColorHex),
                                                        Color(art.accentColorHex)
                                                    )
                                                )
                                            )
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(10.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color.Black.copy(alpha = 0.5f)
                                        ) {
                                            Text(
                                                text = "${art.resolution} UHD",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = art.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "\"${art.prompt}\"",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // GitHub Deployment Guide
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Deploy WanderPulse to GitHub",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ready to share or run on physical Android devices! You can push this project directly to GitHub or download the complete source code using either method below.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    item {
                        ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Method 1: Direct Push from AI Studio UI (Easiest)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "1. In the AI Studio editor header, click the Settings / Export icon in the top right.\n" +
                                        "2. Select 'Push to GitHub'.\n" +
                                        "3. Connect your GitHub account and choose your target repository name.\n" +
                                        "4. Click 'Push' — your full Gradle project, adaptive icons, and source code will be committed!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    item {
                        val gitCommands =
                            "git init\n" +
                            "git add .\n" +
                            "git commit -m \"Initial release of WanderPulse AI Exploration Studio\"\n" +
                            "git branch -M main\n" +
                            "git remote add origin https://github.com/YOUR_USERNAME/wanderpulse.git\n" +
                            "git push -u origin main"

                        ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Method 2: Standard Terminal Git Push",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(gitCommands))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Commands",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = gitCommands,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF38BDF8)
                                        ),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Building the APK for Testing",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "To test the compiled APK directly on your phone:\n" +
                                        "• Click the top-right Settings menu -> 'Download APK'.\n" +
                                        "• Transfer the APK to your phone and tap to install.\n" +
                                        "• All location, audio, camera, and map functions run natively with Android Material 3!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
