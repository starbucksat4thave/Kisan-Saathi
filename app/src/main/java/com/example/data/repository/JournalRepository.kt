package com.example.data.repository

import com.example.data.model.GeneratedPostcard
import com.example.data.model.ItineraryItem
import com.example.data.model.Place
import com.example.data.model.TranscriptionRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class JournalRepository(private val placesRepository: PlacesRepository) {

    private val _itinerary = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val itinerary: StateFlow<List<ItineraryItem>> = _itinerary.asStateFlow()

    private val _transcriptions = MutableStateFlow<List<TranscriptionRecord>>(emptyList())
    val transcriptions: StateFlow<List<TranscriptionRecord>> = _transcriptions.asStateFlow()

    private val _postcards = MutableStateFlow<List<GeneratedPostcard>>(emptyList())
    val postcards: StateFlow<List<GeneratedPostcard>> = _postcards.asStateFlow()

    init {
        // Seed default sample entries
        val defaultPlaces = placesRepository.places.value
        if (defaultPlaces.isNotEmpty()) {
            _itinerary.value = listOf(
                ItineraryItem(
                    id = "itin_1",
                    dayNumber = 1,
                    place = defaultPlaces[0],
                    timeSlot = "09:30 AM - Morning Exploration",
                    customNotes = "Pre-book tickets for summit lift. Carry wide-angle lens for photography."
                ),
                ItineraryItem(
                    id = "itin_2",
                    dayNumber = 1,
                    place = defaultPlaces[1],
                    timeSlot = "02:00 PM - Afternoon Walk",
                    customNotes = "Hike up past the main crossroads for serene, uncrowded forest views."
                )
            )
        }

        _transcriptions.value = listOf(
            TranscriptionRecord(
                id = "trans_1",
                title = "Temple Bell & Bamboo Forest Memo",
                transcriptionText = "We just arrived at the southern overlook of the temple grounds. The morning bell resonance echoes across the bamboo groves, and the cobblestone path is lined with traditional stone lanterns.",
                durationSeconds = 18,
                timestamp = System.currentTimeMillis() - 7200000L
            )
        )

        _postcards.value = listOf(
            GeneratedPostcard(
                id = "art_1",
                title = "Floating Aurora Over Mountain Sanctum",
                prompt = "Luminescent floating shrine over turquoise alpine lake with neon auroras in deep twilight",
                resolution = "2K",
                modelName = "gemini-3-pro-image-preview",
                primaryColorHex = 0xFF0F172A,
                accentColorHex = 0xFF00E5FF
            )
        )
    }

    fun addToItinerary(place: Place, dayNumber: Int, timeSlot: String, notes: String) {
        val newItem = ItineraryItem(
            id = UUID.randomUUID().toString(),
            dayNumber = dayNumber,
            place = place,
            timeSlot = timeSlot,
            customNotes = notes
        )
        _itinerary.value = _itinerary.value + newItem
    }

    fun removeFromItinerary(id: String) {
        _itinerary.value = _itinerary.value.filter { it.id != id }
    }

    fun saveTranscription(title: String, text: String, durationSec: Int) {
        val record = TranscriptionRecord(
            id = UUID.randomUUID().toString(),
            title = title,
            transcriptionText = text,
            durationSeconds = durationSec,
            timestamp = System.currentTimeMillis()
        )
        _transcriptions.value = listOf(record) + _transcriptions.value
    }

    fun deleteTranscription(id: String) {
        _transcriptions.value = _transcriptions.value.filter { it.id != id }
    }

    fun savePostcard(postcard: GeneratedPostcard) {
        _postcards.value = listOf(postcard) + _postcards.value
    }

    fun updatePostcard(postcard: GeneratedPostcard) {
        _postcards.value = _postcards.value.map { if (it.id == postcard.id) postcard else it }
    }

    fun exportTripSummary(): String {
        val builder = StringBuilder()
        builder.appendLine("# 🌍 WanderPulse Expedition Summary")
        builder.appendLine("Generated: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
        builder.appendLine("\n## 📍 Planned Itinerary")
        val days = _itinerary.value.groupBy { it.dayNumber }
        if (days.isEmpty()) {
            builder.appendLine("No itinerary stops scheduled yet.")
        } else {
            days.toSortedMap().forEach { (day, items) ->
                builder.appendLine("\n### Day $day")
                items.forEach { item ->
                    builder.appendLine("- **${item.timeSlot}**: ${item.place.name}")
                    builder.appendLine("  - Location: ${item.place.address}")
                    if (item.customNotes.isNotBlank()) {
                        builder.appendLine("  - Notes: ${item.customNotes}")
                    }
                }
            }
        }

        builder.appendLine("\n## 🎙️ Audio Field Memos (${_transcriptions.value.size})")
        _transcriptions.value.forEach { memo ->
            builder.appendLine("- **${memo.title}** (${memo.durationSeconds}s): ${memo.transcriptionText}")
        }

        builder.appendLine("\n## 🎨 Generated Postcards (${_postcards.value.size})")
        _postcards.value.forEach { art ->
            builder.appendLine("- **${art.title}** [${art.resolution}]: \"${art.prompt}\"")
        }

        return builder.toString()
    }
}
