package com.example.data.repository

import com.example.data.model.GroundedSearchResult
import com.example.data.model.GroundedSource
import com.example.data.model.ImageAnalysisResult
import com.example.data.model.SourceType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AiRepository {
    fun isConnectedToFirebase(): Boolean
    suspend fun converseLive(userMessage: String): Flow<String>
    suspend fun transcribeAudio(durationSeconds: Int): String
    suspend fun searchWithGrounding(query: String, useMapsGrounding: Boolean): GroundedSearchResult
    suspend fun analyzeLandmark(photoDescription: String): ImageAnalysisResult
    suspend fun generateArtPrompt(prompt: String, resolution: String): Pair<Long, Long> // returns primary/accent color codes for visual postcard representation
    suspend fun editArt(prompt: String, editInstruction: String): Pair<Long, Long>
}

class PlaceholderAiRepository : AiRepository {

    override fun isConnectedToFirebase(): Boolean = false

    override suspend fun converseLive(userMessage: String): Flow<String> = flow {
        val responses = listOf(
            "Hello! I am your WanderPulse Live Guide powered by the Gemini 3.8 Live architecture. ",
            "I'm tracking your expedition route in real time. ",
            "Based on the local time and current sun angle, it's an ideal moment to explore nearby historical sites or capture golden-hour photography. ",
            "Would you like me to map out a leisurely scenic walking path or suggest nearby culinary delicacies?"
        )
        for (chunk in responses) {
            delay(350)
            emit(chunk)
        }
    }

    override suspend fun transcribeAudio(durationSeconds: Int): String {
        delay(1200) // Realistic audio processing delay
        val simulatedTranscriptions = listOf(
            "\"We just arrived at the southern overlook of the temple grounds. The morning bell resonance echoes across the bamboo groves, and the cobblestone path is lined with traditional stone lanterns. We should definitely revisit at dusk when the lanterns illuminate the mountain trail.\"",
            "\"Note to travel itinerary: The local artisan bakery just around the corner from the station opens at 7:00 AM sharp. Their cinnamon cardamom brioche and roasted matcha latte were incredible. Highly recommended for tomorrow's early morning excursion.\"",
            "\"Audio memo: Remember to take the scenic funicular railway to the summit observation deck instead of the highway bus. The cliffside panorama across the coastal fjord is completely breathtaking!\""
        )
        return simulatedTranscriptions.random()
    }

    override suspend fun searchWithGrounding(query: String, useMapsGrounding: Boolean): GroundedSearchResult {
        delay(1000)
        return if (useMapsGrounding) {
            GroundedSearchResult(
                query = query,
                summary = "Maps Grounding verified 4 high-rated destinations matching '$query'. Best rated: 4.8★ with average wait times under 15 minutes, accessible transit links, and live opening status confirmed today.",
                sources = listOf(
                    GroundedSource("Google Maps Places: Live Coordinates & Open Hours", "https://maps.google.com", SourceType.GOOGLE_MAPS),
                    GroundedSource("Local Mobility & Transit Timetables", "https://maps.google.com/transit", SourceType.GOOGLE_MAPS)
                )
            )
        } else {
            GroundedSearchResult(
                query = query,
                summary = "Search Grounding retrieved up-to-date travel advisory, seasonal events, and local cultural etiquette for '$query'. Current seasonal conditions are exceptionally favorable with mild temperatures.",
                sources = listOf(
                    GroundedSource("Google Search: Seasonal Festival Schedule & Guide", "https://google.com/search?q=" + query.replace(" ", "+"), SourceType.GOOGLE_SEARCH),
                    GroundedSource("Cultural Heritage & Traveler Access Info", "https://whc.unesco.org", SourceType.GOOGLE_SEARCH)
                )
            )
        }
    }

    override suspend fun analyzeLandmark(photoDescription: String): ImageAnalysisResult {
        delay(1400) // Deep reasoning delay matching gemini-4-argon
        return ImageAnalysisResult(
            landmarkTitle = "Gothic-Renaissance Cathedral & Bell Belfry",
            location = "Historic Old Town District",
            era = "Late 14th – Mid 16th Century",
            architecturalStyle = "High Gothic with Renaissance Flying Buttresses and Rose Windows",
            comprehensiveHistory = "Argon 4.0 Analysis: The structure exhibits hand-chiseled limestone masonry with ornate gargoyles designed as functional rainwater spouts. Surviving centuries of regional conflicts, the central nave boasts rib vaulting reaching 32 meters high, complemented by hand-blown stained glass panels depicting celestial constellations.",
            photographyTips = listOf(
                "Position your frame at a 45-degree angle from the southwest plaza to capture the dramatic spire depth.",
                "Utilize HDR mode or bracket exposure to balance intense exterior sunlight with deep stained-glass contrast.",
                "Arrive 30 minutes before sunrise when ambient architectural floodlights reflect across morning mist."
            ),
            hiddenGemsNearby = listOf(
                "A subterranean 15th-century crypt museum accessible through a small wooden doorway behind the choir apse.",
                "An artisan cloister courtyard garden with medicinal herbs maintained by local botanists since 1640.",
                "A rooftop panoramic terrace accessible via the 280-step north tower spiral staircase."
            ),
            confidenceScore = 0.97f
        )
    }

    override suspend fun generateArtPrompt(prompt: String, resolution: String): Pair<Long, Long> {
        delay(1800) // Image synthesis delay
        val colorPairs = listOf(
            Pair(0xFF0F172A, 0xFF38BDF8), // Midnight Ocean & Cyan
            Pair(0xFF1E1B4B, 0xFFA855F7), // Cosmic Purple & Neon Violet
            Pair(0xFF14532D, 0xFF4ADE80), // Alpine Forest & Emerald Glow
            Pair(0xFF7C2D12, 0xFFFB923C), // Desert Canyon & Golden Sunset
            Pair(0xFF0369A1, 0xFFFBBF24)  // Aegean Coast & Warm Gold
        )
        return colorPairs.random()
    }

    override suspend fun editArt(prompt: String, editInstruction: String): Pair<Long, Long> {
        delay(1500)
        return Pair(0xFF4C1D95, 0xFFF472B6) // Deep amethyst with neon magenta highlights
    }
}
