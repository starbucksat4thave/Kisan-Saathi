package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FarmerProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "Rashid Minhas",
    val phone: String = "+923001234567",
    val language: String = "ur",
    val village: String = "Dhudial",
    val tehsil: String = "Chakwal",
    val level: String = "mehnati_kisan",
    val barkat_total_maund: Double = 6.0,
    val leaderboard_opt_in: Boolean = true,
    val nickname: String = "Rashid Bhai",
    val points: Long = 340,
    val streak_weeks: Long = 3,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FieldPlot(
    @DocumentId val id: String = "bara_khet",
    val userId: String = "",
    val name: String = "Bara khet (بڑا کھیت)",
    val area_acre: Double = 4.0,
    val centroid_lat: Double = 32.93,
    val centroid_lon: Double = 72.86,
    val water_access: String = "rainfed", // rainfed | well
    val soil_texture: String = "loam", // loam | sandy_loam | clay_loam
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CropSeason(
    @DocumentId val id: String = "",
    val userId: String = "",
    val field_id: String = "bara_khet",
    val crop: String = "wheat",
    val variety: String = "Akbar-19 (اکبر ۱۹)",
    val season: String = "rabi_2026_27",
    val sowing_date: String = "2026-11-05",
    val harvest_date: String? = null,
    val yield_maund: Double? = null,
    val status: String = "active", // active | closed
    val notes: String = "",
    val nasa_events: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class DiaryLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val season_id: String = "rabi_2026_27",
    val date: String = "",
    val type: String = "sowing", // sowing | fertilizer | spray | irrigation | weeding | harvest | sale | problem | note
    val detail: String = "",
    val source: String = "tap", // voice | tap | satellite_confirmed
    val matched_advisory_id: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BarkatLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val advisory_id: String = "",
    val maund_estimate: Double = 0.0,
    val loss_factor_id: String = "",
    val basis: String = "",
    val placeholder: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

enum class NodeState {
    DONE,
    CURRENT,
    UPCOMING,
    MISSED
}

enum class AdvisorySeverity {
    INFO,
    ACT,
    ALERT
}

data class MemoryRef(
    val season: String,
    val textUr: String,
    val textEn: String
)

data class BarkatEstimate(
    val maundEstimate: Double,
    val basis: String,
    val placeholder: Boolean = true
)

data class AdvisoryCard(
    val id: String,
    val key: String,
    val nodeKey: String,
    val severity: AdvisorySeverity,
    val titleUr: String,
    val titleEn: String,
    val bodyUr: String,
    val bodyEn: String,
    val audioRes: String = "",
    val actBy: String,
    val sources: List<String>,
    val confidence: String = "high", // high | medium | low
    val memoryRef: MemoryRef? = null,
    val barkatIfDone: BarkatEstimate? = null,
    val gddValue: Double = 0.0,
    val smapMoisture: Double = 0.0,
    val rainForecastMm: Double = 0.0,
    val isCompleted: Boolean = false
)

data class RoadmapNode(
    val key: String,
    val order: Int,
    val titleUr: String,
    val titleEn: String,
    val windowStart: String,
    val windowEnd: String,
    val state: NodeState,
    val icon: String,
    val advisoryKeys: List<String>,
    val gddStage: String
)

data class SummerPlanOption(
    val id: String,
    val nameUr: String,
    val nameEn: String,
    val descriptionUr: String,
    val avgYieldText: String,
    val avgWaterUseMm: Double,
    val soilCoverScore: Int,
    val grossIncomePkr: Int,
    val inputCostPkr: Int,
    val netMarginPkr: Int,
    val badYearLossPkr: Int,
    val cattleFeedScore: Int,
    val erosionRisk: String,
    val winYearsAgainstFallowPct: Int
)

data class HistoricalSeasonData(
    val seasonId: String,
    val seasonLabelUr: String,
    val seasonLabelEn: String,
    val sowingDate: String,
    val harvestDate: String,
    val yieldMaundPerAcre: Double,
    val eventsUr: List<String>,
    val lessonsUr: String,
    val badgeEarned: String? = null
)

data class NdviCurvePoint(
    val stageUr: String,
    val stageEn: String,
    val currentSeason: Float,
    val season2023: Float,
    val season2024: Float,
    val season2025: Float,
    val regionalMedian: Float
)

data class BadgeItem(
    val id: String,
    val titleUr: String,
    val titleEn: String,
    val descriptionUr: String,
    val icon: String,
    val isEarned: Boolean,
    val earnedDate: String? = null
)

data class VillageLeaderboardFarmer(
    val id: String,
    val nickname: String,
    val village: String,
    val score: Int,
    val streakWeeks: Int,
    val rank: Int,
    val barkatTotalMaund: Double
)

enum class PlaceCategory(val displayName: String, val label: String = displayName) {
    ALL("All Destinations", "All"),
    LANDMARKS("Landmarks", "Landmarks"),
    CULTURE("Culture", "Culture"),
    NATURE("Nature", "Nature"),
    FOOD("Food & Dining", "Food & Dining"),
    TRANSIT("Transit", "Transit")
}

data class Place(
    val id: String = "",
    val name: String = "",
    val category: PlaceCategory = PlaceCategory.LANDMARKS,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val description: String = "",
    val openingHours: String = "",
    val tags: List<String> = emptyList(),
    val weatherSummary: String = "",
    val isFavorite: Boolean = false
)

data class ItineraryItem(
    val id: String = "",
    val dayNumber: Int = 1,
    val place: Place = Place(),
    val timeSlot: String = "",
    val customNotes: String = ""
)

data class TranscriptionRecord(
    val id: String = "",
    val title: String = "",
    val transcriptionText: String = "",
    val durationSeconds: Int = 0,
    val language: String = "English (auto-detected)",
    val confidence: Float = 0.98f,
    val timestamp: Long = 0L
)

data class GeneratedPostcard(
    val id: String = "",
    val title: String = "",
    val prompt: String = "",
    val resolution: String = "1K",
    val modelName: String = "gemini-3-pro-image-preview",
    val primaryColorHex: Long = 0xFF0F172AL,
    val accentColorHex: Long = 0xFF38BDF8L
)

enum class SourceType {
    GOOGLE_MAPS,
    GOOGLE_SEARCH
}

data class GroundedSource(
    val title: String = "",
    val url: String = "",
    val type: SourceType = SourceType.GOOGLE_SEARCH
)

data class GroundedSearchResult(
    val query: String = "",
    val summary: String = "",
    val sources: List<GroundedSource> = emptyList()
)

data class ImageAnalysisResult(
    val landmarkTitle: String = "",
    val location: String = "",
    val era: String = "",
    val architecturalStyle: String = "",
    val comprehensiveHistory: String = "",
    val photographyTips: List<String> = emptyList(),
    val hiddenGemsNearby: List<String> = emptyList(),
    val confidenceScore: Float = 0.95f
)

data class VoiceConversationMessage(
    val id: String = "",
    val text: String = "",
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

enum class GeminiModelTier(
    val title: String,
    val badge: String,
    val description: String,
    val modelId: String
) {
    FLASH("Gemini 3.8 Flash", "General & Fast", "Balanced speed and intelligence for general tasks and map grounding", "gemini-3.8-flash"),
    ARGON("Gemini 4 Argon", "Deep Reasoning", "For complex problem solving, coding, and comprehensive analysis", "gemini-4-argon"),
    FLASH_LITE("Gemini 3.1 Flash-Lite", "Ultra Fast", "Sub-second low latency inference for high volume interactions", "gemini-3.1-flash-lite"),
    LIVE("Gemini 3.8 Live", "Real-Time Voice", "Bidirectional voice interaction and real-time conversation", "gemini-3.8-live"),
    TRANSCRIBE("Gemini 3.5 Transcribe", "Audio Transcription", "High accuracy voice-to-text transcription", "gemini-3.5-transcribe"),
    NANO_IMAGE("Gemini Nano Banana 2.1", "Image Studio", "Create and edit high-resolution images and postcards", "gemini-nano-banana-2.1")
}

