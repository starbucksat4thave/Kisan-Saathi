package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.KisanAiService
import com.example.ai.PestDiagnosisResult
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
fun AskScreen(
    aiService: KisanAiService,
    audioHelper: AudioHelper,
    modifier: Modifier = Modifier
) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    val coroutineScope = rememberCoroutineScope()
    var userQueryText by remember { mutableStateOf("") }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var isThinking by remember { mutableStateOf(false) }
    var aiAnswerText by remember {
        mutableStateOf(
            if (isUrdu) "السلام علیکم راشد بھائی! زراعت، بوائی، کھاد یا فصل کی بیماری سے متعلق اپنا سوال بولیں یا لکھیں۔ کسان ساتھی آپ کی خدمت کے لیے حاضر ہے۔"
            else "Assalam-o-Alaikum Rashid Bhai! Speak or write any question regarding wheat sowing, fertilizer, or crop diseases. Kisan Saathi is here to assist you."
        )
    }

    var diagnosisResult by remember { mutableStateOf<PestDiagnosisResult?>(null) }
    var isDiagnosingPhoto by remember { mutableStateOf(false) }
    var escalatedToOfficer by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val micPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("ask_screen")
    ) {
        Text(
            text = if (isUrdu) "کسان ساتھی سے پوچھیں (Voice Companion)" else "Ask Kisan Saathi (Voice Companion)",
            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
            fontSize = if (isUrdu) 24.sp else 21.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isUrdu) "پوٹھوہار کے بارانی حالات، زرعی تحقیق اور ناسا ڈیٹا کی روشنی میں جواب"
                else "Voice answers grounded in Pothohar rainfed agronomy and NASA Earth observations",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Center Voice "Hold to Talk" Mic Section
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRecordingVoice) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .scale(micPulse)
                                .clip(CircleShape)
                                .background(FieldGreen.copy(alpha = 0.25f))
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (!isRecordingVoice) {
                                    isRecordingVoice = true
                                    coroutineScope.launch {
                                        // Simulate voice recording and speech recognition
                                        delay(2500)
                                        isRecordingVoice = false
                                        userQueryText = "کیا اس وقت گندم میں یوریا کھاد ڈالنا ٹھیک رہے گا؟"
                                        isThinking = true
                                        val answer = aiService.askAgronomyQuestion(userQueryText)
                                        aiAnswerText = answer
                                        isThinking = false
                                        audioHelper.speak(answer)
                                    }
                                } else {
                                    isRecordingVoice = false
                                }
                            }
                            .testTag("hold_to_talk_mic_btn"),
                        color = if (isRecordingVoice) Color(0xFFD32F2F) else FieldGreen,
                        shadowElevation = 6.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isRecordingVoice) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "Mic",
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isRecordingVoice) {
                        if (isUrdu) "آواز ریکارڈ ہو رہی ہے... (سن رہا ہوں)" else "Recording voice... (Listening)"
                    } else {
                        if (isUrdu) "مائیک دبائیں اور سوال بولیں (Tap to Speak)" else "Tap to speak question"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRecordingVoice) Color(0xFFD32F2F) else FieldGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // AI Answer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF1F8E9)
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FieldGreen.copy(alpha = 0.35f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Hearing, contentDescription = null, tint = FieldGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUrdu) "کسان ساتھی کا جواب:" else "Kisan Saathi Response:",
                            fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = FieldGreen
                        )
                    }

                    IconButton(
                        onClick = { audioHelper.speak(aiAnswerText) },
                        modifier = Modifier.testTag("speak_answer_btn")
                    ) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Listen", tint = FieldGreen)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (isThinking) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = FieldGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isUrdu) "ناسا ڈیٹا اور زرعی رہنمائی سے تجزیہ ہو رہا ہے..." else "Analyzing with NASA Earth data & agronomy models...",
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Text(
                        text = aiAnswerText,
                        fontSize = 16.sp,
                        lineHeight = 26.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isUrdu) "ماخذ: بارانی ریسرچ انسٹیٹیوٹ (BARI) چکوال سفارشات + ناسا موسمیاتی ریکارڈ"
                        else "Source: BARI Chakwal Agronomy + NASA POWER Climatology",
                    fontSize = 11.sp,
                    color = Color(0xFF2E7D32)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Text query fallback input
        OutlinedTextField(
            value = userQueryText,
            onValueChange = { userQueryText = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ask_query_input"),
            label = { Text(if (isUrdu) "یا سوال یہاں لکھیں (Type question)..." else "Or type your question here...") },
            shape = RoundedCornerShape(14.dp),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (userQueryText.isNotBlank()) {
                            coroutineScope.launch {
                                isThinking = true
                                val ans = aiService.askAgronomyQuestion(userQueryText)
                                aiAnswerText = ans
                                isThinking = false
                                audioHelper.speak(ans)
                            }
                        }
                    },
                    modifier = Modifier.testTag("send_query_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = FieldGreen)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Camera / Pest Diagnosis Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = WheatGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "کیمرہ معائنہ: کیڑا یا بیماری کی تصویر لیں" else "Crop Scout: Photo Pest & Disease Scan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isUrdu) "فصل کے متاثرہ پتے کی تصویر کھینچیں؛ ناسا اور جیمینائی وژن فوری معائنہ کر کے غیر ضروری اسپرے سے بچائے گا۔"
                        else "Take a leaf photo; Gemini Vision & NASA agronomy analyze issues to prevent unnecessary spraying.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            isDiagnosingPhoto = true
                            // Create a synthetic leaf sample bitmap representing field leaf
                            val bmp = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
                            val c = Canvas(bmp)
                            val p = Paint().apply { color = android.graphics.Color.rgb(100, 180, 80) }
                            c.drawRect(0f, 0f, 200f, 200f, p)

                            val res = aiService.diagnoseCropPhoto(bmp)
                            diagnosisResult = res
                            isDiagnosingPhoto = false
                            audioHelper.speak(res.recommendationUr)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("scan_crop_photo_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WheatGold)
                ) {
                    if (isDiagnosingPhoto) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isUrdu) "تصویر کا معائنہ ہو رہا ہے..." else "Analyzing leaf photo...")
                    } else {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isUrdu) "پتے کا اسکین کریں (Photo Scout)" else "Scan Leaf Photo (Scout)")
                    }
                }

                diagnosisResult?.let { res ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FieldGreen.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = FieldGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = res.issueNameUr, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FieldGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isUrdu) "· شدت: ${res.severityLevel}" else "· Severity: ${res.severityLevel}",
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = res.recommendationUr, fontSize = 13.sp, lineHeight = 20.sp, color = Color(0xFF1B5E20))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Escalate to Agri Officer Queue
                    OutlinedButton(
                        onClick = {
                            escalatedToOfficer = true
                            coroutineScope.launch {
                                delay(3000)
                                escalatedToOfficer = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("escalate_officer_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = FieldGreen)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (escalatedToOfficer) {
                                if (isUrdu) "تحصیل زراعت افسر (چکوال) کو بھیج دیا گیا ✓" else "Sent to Extension Officer (Chakwal) ✓"
                            } else {
                                if (isUrdu) "زراعت افسر کو معائنہ بھیجیں (Escalate to Agri Officer)" else "Escalate to Agri Officer"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FieldGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
