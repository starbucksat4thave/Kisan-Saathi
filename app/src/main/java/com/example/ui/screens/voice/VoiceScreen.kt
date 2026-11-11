package com.example.ui.screens.voice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoiceConversationMessage
import com.example.data.repository.AiRepository
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.DemoModeNoticeBanner
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun VoiceScreen(
    aiRepository: AiRepository
) {
    val messages = remember {
        mutableStateListOf(
            VoiceConversationMessage(
                id = "msg_0",
                text = "Welcome to WanderPulse Live Voice! Tap the microphone below or select a starter prompt to talk with gemini-3.8-live in real-time.",
                isUser = false
            )
        )
    }

    var isListening by remember { mutableStateOf(false) }
    var isModelSpeaking by remember { mutableStateOf(false) }
    var textInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val micHaloScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micHalo"
    )

    fun sendVoiceTurn(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = VoiceConversationMessage(
            id = UUID.randomUUID().toString(),
            text = prompt,
            isUser = true
        )
        messages.add(userMsg)
        isListening = false
        isModelSpeaking = true

        scope.launch {
            val responseBuilder = StringBuilder()
            val aiMsgId = UUID.randomUUID().toString()
            val aiMsg = VoiceConversationMessage(
                id = aiMsgId,
                text = "Thinking...",
                isUser = false
            )
            messages.add(aiMsg)

            aiRepository.converseLive(prompt).collect { chunk ->
                responseBuilder.append(chunk)
                val index = messages.indexOfFirst { it.id == aiMsgId }
                if (index != -1) {
                    messages[index] = aiMsg.copy(text = responseBuilder.toString())
                }
            }
            isModelSpeaking = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("voice_screen")
    ) {
        DemoModeNoticeBanner()

        // Live Audio API Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isListening || isModelSpeaking) Color(0xFF10B981) else MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isModelSpeaking) "Gemini 3.8 Live Speaking..." else if (isListening) "Listening to speech..." else "Live Audio API Ready",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "gemini-3.8-live",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AudioWaveformVisualizer(
                    isRecording = isListening || isModelSpeaking,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        // Conversation History
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                VoiceMessageBubble(message = msg)
            }
        }

        // Quick starter prompts (if not recording)
        AnimatedVisibility(visible = !isListening && !isModelSpeaking) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Quick Inquiries:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { sendVoiceTurn("What is the history of this monument?") }
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = "🏛️ Monument History",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { sendVoiceTurn("Where is the best sunset viewpoint?") }
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = "🌅 Sunset Spot",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // Audio controls & Mic Action Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Mic Button with Animated Halo
                Box(contentAlignment = Alignment.Center) {
                    if (isListening) {
                        Box(
                            modifier = Modifier
                                .size(76.dp * micHaloScale)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                            .clickable {
                                if (isListening) {
                                    sendVoiceTurn("Tell me about local customs and secret viewpoints nearby.")
                                } else {
                                    isListening = true
                                }
                            }
                            .testTag("voice_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isListening) "Stop Listening" else "Start Talking",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isListening) "Tap to send speech • Listening..." else "Tap Mic to Talk Live",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Text alternate fallback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("voice_text_fallback_input"),
                        placeholder = { Text("Or type a travel question...", fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                sendVoiceTurn(textInput)
                                textInput = ""
                            }
                        },
                        enabled = textInput.isNotBlank(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ask")
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceMessageBubble(message: VoiceConversationMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (message.isUser) "You (Voice)" else "Gemini 3.8 Live",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (message.isUser) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primary
                    )
                    if (!message.isUser) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Audio output",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
