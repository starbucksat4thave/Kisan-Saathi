package com.example.data.repository

import com.example.data.model.Place
import com.example.data.model.PlaceCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PlacesRepository {

    private val initialPlaces = listOf(
        Place(
            id = "place_1",
            name = "Eiffel Tower & Champ de Mars",
            category = PlaceCategory.LANDMARKS,
            latitude = 48.8584,
            longitude = 2.2945,
            address = "Champ de Mars, 5 Av. Anatole France, 75007 Paris, France",
            rating = 4.7f,
            reviewCount = 328000,
            description = "Iconic wrought-iron lattice tower on the Champ de Mars, built in 1889 for the Exposition Universelle.",
            openingHours = "09:30 AM - 11:45 PM",
            tags = listOf("Iconic", "Panoramic Views", "Architecture", "Sunset Spot"),
            weatherSummary = "18°C • Partly Cloudy",
            isFavorite = true
        ),
        Place(
            id = "place_2",
            name = "Fushimi Inari-taisha",
            category = PlaceCategory.CULTURE,
            latitude = 34.9671,
            longitude = 135.7727,
            address = "68 Fukakusa Yabunouchicho, Fushimi Ward, Kyoto, Japan",
            rating = 4.8f,
            reviewCount = 54200,
            description = "Shinto shrine renowned for its thousands of vibrant vermilion torii gates winding through scenic mountain trails.",
            openingHours = "Open 24 Hours",
            tags = listOf("Sacred", "Torii Gates", "Forest Trail", "Spiritual"),
            weatherSummary = "20°C • Sunny",
            isFavorite = true
        ),
        Place(
            id = "place_3",
            name = "Colosseum & Roman Forum",
            category = PlaceCategory.LANDMARKS,
            latitude = 41.8902,
            longitude = 12.4922,
            address = "Piazza del Colosseo, 1, 00184 Roma RM, Italy",
            rating = 4.7f,
            reviewCount = 289000,
            description = "The largest ancient amphitheatre ever built, hosting gladiatorial contests and public spectacles in Imperial Rome.",
            openingHours = "08:30 AM - 07:15 PM",
            tags = listOf("Ancient History", "UNESCO", "Gladiators", "Spectacular"),
            weatherSummary = "24°C • Sunny"
        ),
        Place(
            id = "place_4",
            name = "Grand Canyon National Park",
            category = PlaceCategory.NATURE,
            latitude = 36.1069,
            longitude = -112.1129,
            address = "Arizona, United States",
            rating = 4.9f,
            reviewCount = 142000,
            description = "Immense natural canyon carved by the Colorado River, exposing millions of years of colorful geological strata.",
            openingHours = "Open 24 Hours",
            tags = listOf("Natural Wonder", "Hiking", "Canyon", "Sunrise"),
            weatherSummary = "22°C • Clear"
        ),
        Place(
            id = "place_5",
            name = "Tsukiji Outer Market",
            category = PlaceCategory.FOOD,
            latitude = 35.6655,
            longitude = 139.7707,
            address = "4 Chome-16-2 Tsukiji, Chuo City, Tokyo 104-0045, Japan",
            rating = 4.6f,
            reviewCount = 68400,
            description = "Bustling foodie destination packed with street food stalls, fresh sushi bars, wagyu skewers, and culinary treasures.",
            openingHours = "05:00 AM - 02:00 PM",
            tags = listOf("Street Food", "Fresh Seafood", "Matcha", "Culinary"),
            weatherSummary = "19°C • Mild"
        ),
        Place(
            id = "place_6",
            name = "Grand Central Terminal",
            category = PlaceCategory.TRANSIT,
            latitude = 40.7527,
            longitude = -73.9772,
            address = "89 E 42nd St, New York, NY 10017, United States",
            rating = 4.7f,
            reviewCount = 115000,
            description = "Historic Beaux-Arts transit landmark featuring astronomical ceiling murals, whispering galleries, and gourmet food hall.",
            openingHours = "05:15 AM - 02:00 AM",
            tags = listOf("Beaux-Arts", "Hub", "History", "Food Hall"),
            weatherSummary = "16°C • Clear"
        ),
        Place(
            id = "place_7",
            name = "Great Pyramid of Giza & Sphinx",
            category = PlaceCategory.LANDMARKS,
            latitude = 29.9792,
            longitude = 31.1342,
            address = "Al Haram, Nazlet El-Semman, Al Giza Desert, Giza Governorate, Egypt",
            rating = 4.8f,
            reviewCount = 98000,
            description = "Oldest of the Seven Wonders of the Ancient World, monumental tombs dating back over 4,500 years.",
            openingHours = "08:00 AM - 05:00 PM",
            tags = listOf("Ancient Wonder", "Pharaohs", "Desert", "Monument"),
            weatherSummary = "29°C • Warm"
        ),
        Place(
            id = "place_8",
            name = "Café de Flore",
            category = PlaceCategory.FOOD,
            latitude = 48.8542,
            longitude = 2.3328,
            address = "172 Bd Saint-Germain, 75006 Paris, France",
            rating = 4.4f,
            reviewCount = 21000,
            description = "Fabled Left Bank literary cafe frequented by philosophers, writers, and artists since the 1880s.",
            openingHours = "07:30 AM - 01:30 AM",
            tags = listOf("Literary Cafe", "Espresso", "Croissants", "Historic"),
            weatherSummary = "18°C • Partly Cloudy"
        )
    )

    private val _places = MutableStateFlow(initialPlaces)
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    fun toggleFavorite(placeId: String) {
        _places.value = _places.value.map { place ->
            if (place.id == placeId) place.copy(isFavorite = !place.isFavorite) else place
        }
    }

    fun searchPlaces(query: String, category: PlaceCategory): List<Place> {
        return _places.value.filter { place ->
            val matchesCategory = category == PlaceCategory.ALL || place.category == category
            val matchesQuery = query.isBlank() ||
                place.name.contains(query, ignoreCase = true) ||
                place.address.contains(query, ignoreCase = true) ||
                place.tags.any { it.contains(query, ignoreCase = true) }
            matchesCategory && matchesQuery
        }
    }

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
