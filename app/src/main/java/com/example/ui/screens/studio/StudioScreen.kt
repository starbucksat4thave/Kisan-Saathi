package com.example.ui.screens.studio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush as GradientBrush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratedPostcard
import com.example.data.model.ImageAnalysisResult
import com.example.data.repository.AiRepository
import com.example.data.repository.JournalRepository
import com.example.ui.components.DemoModeNoticeBanner
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun StudioScreen(
    aiRepository: AiRepository,
    journalRepository: JournalRepository
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Vision Analysis, 1 = Generate & Edit Image

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("studio_screen")
    ) {
        DemoModeNoticeBanner()

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Photo Vision (Argon 4.0)") },
                icon = { Icon(Icons.Default.PhotoCamera, contentDescription = null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("AI Art & Edit (1K-4K)") },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) }
            )
        }

        if (selectedTab == 0) {
            VisionAnalysisTab(aiRepository = aiRepository)
        } else {
            ImageGenAndEditTab(
                aiRepository = aiRepository,
                journalRepository = journalRepository
            )
        }
    }
}

@Composable
fun VisionAnalysisTab(aiRepository: AiRepository) {
    val samplePhotos = listOf(
        "Ancient European Cathedral & Spire",
        "Japanese Shrine Torii in Forest",
        "Ancient Pyramid at Sunset",
        "Historic Italian Colosseum Amphitheatre"
    )

    var selectedSample by remember { mutableStateOf(samplePhotos[0]) }
    var userCustomDescription by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<ImageAnalysisResult?>(null) }
    val scope = rememberCoroutineScope()

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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Multimodal Landmark Analyzer",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Model: gemini-4-argon (Frontier Vision)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Argon 4.0",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Select a landmark photo or describe a scene to analyze:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(samplePhotos) { sample ->
                            FilterChip(
                                selected = selectedSample == sample,
                                onClick = {
                                    selectedSample = sample
                                    analysisResult = null
                                },
                                label = { Text(sample, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = userCustomDescription,
                        onValueChange = { userCustomDescription = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Or enter specific questions about architecture/monument...", fontSize = 13.sp) },
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            isAnalyzing = true
                            scope.launch {
                                val query = userCustomDescription.ifBlank { selectedSample }
                                analysisResult = aiRepository.analyzeLandmark(query)
                                isAnalyzing = false
                            }
                        },
                        enabled = !isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("analyze_photo_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing with gemini-4-argon...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyze with Gemini Argon")
                        }
                    }
                }
            }
        }

        // Analysis Results Card
        analysisResult?.let { res ->
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = res.landmarkTitle,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${res.location} • ${res.era}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "${(res.confidenceScore * 100).toInt()}% Match",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Architectural Style: ${res.architecturalStyle}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = res.comprehensiveHistory,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "📸 Pro Photography & Framing Tips:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        res.photographyTips.forEach { tip ->
                            Text(
                                text = "• $tip",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "💎 Hidden Gems Within 5 Minutes:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        res.hiddenGemsNearby.forEach { gem ->
                            Text(
                                text = "• $gem",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageGenAndEditTab(
    aiRepository: AiRepository,
    journalRepository: JournalRepository
) {
    val resolutionOptions = listOf("1K", "2K", "4K")
    var selectedResolution by remember { mutableStateOf("2K") }
    var promptInput by remember { mutableStateOf("Luminescent floating shrine over turquoise alpine lake with neon auroras in deep twilight") }
    var editInstruction by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    var currentPrimaryColor by remember { mutableStateOf(0xFF0F172A) }
    var currentAccentColor by remember { mutableStateOf(0xFF00E5FF) }
    var currentPostcardTitle by remember { mutableStateOf("Cosmic Shrine at Twilight") }
    var currentPostcardId by remember { mutableStateOf("gen_${UUID.randomUUID()}") }
    var isSavedToJournal by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val savedPostcards by journalRepository.postcards.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Generation Configuration Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "High-Quality Travel Studio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Models: gemini-3-pro-image-preview & nano-banana-2.1",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = selectedResolution,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Output Image Resolution Affordance:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 1K, 2K, 4K Affordance selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        resolutionOptions.forEach { res ->
                            FilterChip(
                                selected = selectedResolution == res,
                                onClick = { selectedResolution = res },
                                label = { Text("$res UHD", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.weight(1f).testTag("resolution_chip_$res")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("art_prompt_input"),
                        label = { Text("Creation Prompt") },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            isGenerating = true
                            scope.launch {
                                val colors = aiRepository.generateArtPrompt(promptInput, selectedResolution)
                                currentPrimaryColor = colors.first
                                currentAccentColor = colors.second
                                currentPostcardTitle = promptInput.take(28) + "..."
                                currentPostcardId = "gen_${UUID.randomUUID()}"
                                isSavedToJournal = false
                                isGenerating = false
                            }
                        },
                        enabled = !isGenerating && promptInput.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_art_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Synthesizing $selectedResolution Art...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate $selectedResolution Postcard")
                        }
                    }
                }
            }
        }

        // Live Generated Art Canvas Canvas Preview
        item {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentPostcardTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Resolution: $selectedResolution • gemini-3-pro-image-preview",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Postcard Artwork Visual Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                GradientBrush.verticalGradient(
                                    listOf(
                                        Color(currentPrimaryColor),
                                        Color(currentAccentColor)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw celestial stars & aurora arcs
                            drawCircle(
                                color = Color.White.copy(alpha = 0.8f),
                                radius = 24.dp.toPx(),
                                center = Offset(w * 0.75f, h * 0.35f)
                            )

                            // Mountain Silhouette
                            val mountainPath = androidx.compose.ui.graphics.Path().apply {
                                moveTo(0f, h)
                                lineTo(w * 0.25f, h * 0.45f)
                                lineTo(w * 0.5f, h * 0.75f)
                                lineTo(w * 0.78f, h * 0.4f)
                                lineTo(w, h * 0.65f)
                                lineTo(w, h)
                                close()
                            }
                            drawPath(mountainPath, color = Color.Black.copy(alpha = 0.45f))
                        }

                        // Postcard stamp watermark
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.45f)
                        ) {
                            Text(
                                text = "$selectedResolution UHD",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Text-based Image Editing Affordance (gemini-nano-banana-2.1)
                    Text(
                        text = "Edit Postcard with Text (gemini-nano-banana-2.1):",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = editInstruction,
                            onValueChange = { editInstruction = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("image_edit_instruction_input"),
                            placeholder = { Text("e.g., 'Add glowing neon constellation'", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (editInstruction.isNotBlank()) {
                                    isEditing = true
                                    scope.launch {
                                        val colors = aiRepository.editArt(promptInput, editInstruction)
                                        currentPrimaryColor = colors.first
                                        currentAccentColor = colors.second
                                        isEditing = false
                                        editInstruction = ""
                                    }
                                }
                            },
                            enabled = !isEditing && editInstruction.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("apply_image_edit_button")
                        ) {
                            if (isEditing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                            } else {
                                Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Apply", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                val postcard = GeneratedPostcard(
                                    id = currentPostcardId,
                                    title = currentPostcardTitle,
                                    prompt = promptInput,
                                    resolution = selectedResolution,
                                    primaryColorHex = currentPrimaryColor,
                                    accentColorHex = currentAccentColor
                                )
                                journalRepository.savePostcard(postcard)
                                isSavedToJournal = true
                            },
                            enabled = !isSavedToJournal,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("save_postcard_button")
                        ) {
                            Icon(
                                imageVector = if (isSavedToJournal) Icons.Default.Check else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isSavedToJournal) "Saved to Journal" else "Save Postcard")
                        }
                    }
                }
            }
        }
    }
}
