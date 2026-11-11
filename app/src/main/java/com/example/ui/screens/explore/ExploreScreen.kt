package com.example.ui.screens.explore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GroundedSearchResult
import com.example.data.model.Place
import com.example.data.model.PlaceCategory
import com.example.data.repository.AiRepository
import com.example.data.repository.JournalRepository
import com.example.data.repository.PlacesRepository
import com.example.ui.components.DemoModeNoticeBanner
import com.example.ui.components.GroundedSourceItem
import kotlinx.coroutines.launch

@Composable
fun ExploreScreen(
    placesRepository: PlacesRepository,
    journalRepository: JournalRepository,
    aiRepository: AiRepository
) {
    val places by placesRepository.places.collectAsState()
    var selectedCategory by remember { mutableStateOf(PlaceCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }

    // Grounding states
    var useMapsGrounding by remember { mutableStateOf(true) }
    var useSearchGrounding by remember { mutableStateOf(true) }
    var isSearchingGrounded by remember { mutableStateOf(false) }
    var groundedResult by remember { mutableStateOf<GroundedSearchResult?>(null) }
    val scope = rememberCoroutineScope()

    // Map viewport state
    var mapZoom by remember { mutableFloatStateOf(1.0f) }
    var mapPanX by remember { mutableFloatStateOf(0f) }
    var mapPanY by remember { mutableFloatStateOf(0f) }

    val filteredPlaces = remember(places, searchQuery, selectedCategory) {
        placesRepository.searchPlaces(searchQuery, selectedCategory)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("explore_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            DemoModeNoticeBanner()
        }

        // Search & Grounding Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_places_input"),
                        placeholder = { Text("Search landmarks, hidden spots, cafes...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Grounding Tools Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilterChip(
                                selected = useMapsGrounding,
                                onClick = { useMapsGrounding = !useMapsGrounding },
                                label = { Text("Maps Data", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                modifier = Modifier.testTag("maps_grounding_toggle")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            FilterChip(
                                selected = useSearchGrounding,
                                onClick = { useSearchGrounding = !useSearchGrounding },
                                label = { Text("Search Data", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                modifier = Modifier.testTag("search_grounding_toggle")
                            )
                        }

                        Button(
                            onClick = {
                                val queryToRun = searchQuery.ifBlank { "Top historic landmarks & secret viewpoints" }
                                isSearchingGrounded = true
                                scope.launch {
                                    groundedResult = aiRepository.searchWithGrounding(
                                        query = queryToRun,
                                        useMapsGrounding = useMapsGrounding
                                    )
                                    isSearchingGrounded = false
                                }
                            },
                            enabled = !isSearchingGrounded,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("grounded_ai_lookup_button")
                        ) {
                            if (isSearchingGrounded) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Text("Ask Gemini", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Grounded Results Card (if active)
        item {
            AnimatedVisibility(visible = groundedResult != null) {
                groundedResult?.let { res ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("grounded_ai_result_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (useMapsGrounding) Icons.Default.LocationOn else Icons.Default.Public,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (useMapsGrounding) "Google Maps Grounded Insights" else "Google Search Grounded Insights",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                IconButton(
                                    onClick = { groundedResult = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = res.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Verified Live Citations:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            res.sources.forEach { source ->
                                GroundedSourceItem(source = source)
                            }
                        }
                    }
                }
            }
        }

        // Interactive Map Canvas Area
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("interactive_map_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    InteractiveMapCanvas(
                        places = filteredPlaces,
                        selectedPlace = selectedPlace,
                        zoom = mapZoom,
                        panX = mapPanX,
                        panY = mapPanY,
                        onTransform = { panChange, zoomChange ->
                            mapPanX += panChange.x
                            mapPanY += panChange.y
                            mapZoom = (mapZoom * zoomChange).coerceIn(0.7f, 3.0f)
                        },
                        onPlaceClicked = { clickedPlace ->
                            selectedPlace = clickedPlace
                        }
                    )

                    // Map overlay controls (Zoom in, Zoom out, Recenter)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { mapZoom = (mapZoom * 1.25f).coerceAtMost(3.0f) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("+", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { mapZoom = (mapZoom / 1.25f).coerceAtLeast(0.7f) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("−", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable {
                                    mapZoom = 1.0f
                                    mapPanX = 0f
                                    mapPanY = 0f
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.NearMe, contentDescription = "Recenter", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Map status pill
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${filteredPlaces.size} locations • Pan & zoom",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PlaceCategory.values()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category.displayName) },
                        modifier = Modifier.testTag("filter_chip_${category.name.lowercase()}")
                    )
                }
            }
        }

        // Places Cards Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Discovered Destinations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${filteredPlaces.size} Results",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Places List
        items(filteredPlaces, key = { it.id }) { place ->
            PlaceCardItem(
                place = place,
                isSelected = selectedPlace?.id == place.id,
                onCardClick = { selectedPlace = place },
                onToggleFavorite = { placesRepository.toggleFavorite(place.id) },
                onAddToItinerary = {
                    journalRepository.addToItinerary(
                        place = place,
                        dayNumber = 1,
                        timeSlot = "02:00 PM - Afternoon Tour",
                        notes = "Added from Map Discovery"
                    )
                }
            )
        }
    }

    // Selected Place Detailed Bottom Sheet / Dialog
    selectedPlace?.let { place ->
        PlaceDetailDialog(
            place = place,
            onDismiss = { selectedPlace = null },
            onToggleFavorite = { placesRepository.toggleFavorite(place.id) },
            onAddToItinerary = {
                journalRepository.addToItinerary(
                    place = place,
                    dayNumber = 1,
                    timeSlot = "10:00 AM - Morning Visit",
                    notes = "Scheduled via WanderPulse Map"
                )
                selectedPlace = null
            }
        )
    }
}

@Composable
fun InteractiveMapCanvas(
    places: List<Place>,
    selectedPlace: Place?,
    zoom: Float,
    panX: Float,
    panY: Float,
    onTransform: (Offset, Float) -> Unit,
    onPlaceClicked: (Place) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val roadColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
    val userPinColor = MaterialTheme.colorScheme.primary
    val pinColor = MaterialTheme.colorScheme.secondary
    val selectedPinColor = Color(0xFFF43F5E)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, gestureZoom, _ ->
                    onTransform(pan, gestureZoom)
                }
            }
            .pointerInput(places) {
                detectTapGestures { tapOffset ->
                    // Find nearest place to tap
                    val centerX = size.width / 2f + panX
                    val centerY = size.height / 2f + panY
                    var clicked: Place? = null
                    var minDist = Float.MAX_VALUE

                    places.forEachIndexed { index, place ->
                        val angle = (index.toFloat() / places.size) * 2.0 * Math.PI
                        val radius = (60f + (index % 3) * 35f) * zoom
                        val px = (centerX + Math.cos(angle) * radius).toFloat()
                        val py = (centerY + Math.sin(angle) * radius).toFloat()

                        val dist = (tapOffset.x - px) * (tapOffset.x - px) + (tapOffset.y - py) * (tapOffset.y - py)
                        if (dist < 40f * 40f && dist < minDist) {
                            minDist = dist
                            clicked = place
                        }
                    }
                    clicked?.let { onPlaceClicked(it) }
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f + panX
        val centerY = height / 2f + panY

        // Background terrain fill
        drawRect(color = surfaceColor)

        // Draw topographic grid lines
        val step = 40f * zoom
        var x = (centerX % step)
        while (x < width) {
            drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)
            x += step
        }
        var y = (centerY % step)
        while (y < height) {
            drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
            y += step
        }

        // Draw arterial roads / exploration paths
        val roadPath = Path().apply {
            moveTo(0f, centerY + 30f * zoom)
            cubicTo(
                width * 0.3f, centerY - 60f * zoom,
                width * 0.7f, centerY + 80f * zoom,
                width, centerY - 20f * zoom
            )
        }
        drawPath(roadPath, color = roadColor, style = Stroke(width = 8f * zoom))

        // Draw secondary diagonal road
        drawLine(
            color = roadColor,
            start = Offset(centerX - 120f * zoom, 0f),
            end = Offset(centerX + 120f * zoom, height),
            strokeWidth = 6f * zoom
        )

        // Draw User GPS location with pulsating halo
        drawCircle(
            color = userPinColor.copy(alpha = pulseAlpha * 0.4f),
            radius = 24f * zoom,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = Color.White,
            radius = 9f * zoom,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = userPinColor,
            radius = 6f * zoom,
            center = Offset(centerX, centerY)
        )

        // Draw destination pins around map
        places.forEachIndexed { index, place ->
            val angle = (index.toFloat() / places.size) * 2.0 * Math.PI
            val radius = (60f + (index % 3) * 35f) * zoom
            val px = (centerX + Math.cos(angle) * radius).toFloat()
            val py = (centerY + Math.sin(angle) * radius).toFloat()

            val isSelected = selectedPlace?.id == place.id
            val currentColor = if (isSelected) selectedPinColor else pinColor

            if (isSelected) {
                drawCircle(
                    color = selectedPinColor.copy(alpha = 0.3f),
                    radius = 20f * zoom,
                    center = Offset(px, py)
                )
            }

            // Pin base dot
            drawCircle(
                color = Color.White,
                radius = 10f * zoom,
                center = Offset(px, py)
            )
            drawCircle(
                color = currentColor,
                radius = 7.5f * zoom,
                center = Offset(px, py)
            )
        }
    }
}

@Composable
fun PlaceCardItem(
    place: Place,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToItinerary: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onCardClick() }
            .testTag("place_card_${place.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = place.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp).testTag("fav_button_${place.id}")
                ) {
                    Icon(
                        imageVector = if (place.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (place.isFavorite) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = place.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${place.rating}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${place.reviewCount / 1000}k)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = place.weatherSummary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Button(
                    onClick = onAddToItinerary,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.testTag("add_itinerary_${place.id}")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Itinerary", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun PlaceDetailDialog(
    place: Place,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToItinerary: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {}, // prevent click-through
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${place.latitude.toString().take(7)}° N, ${place.longitude.toString().take(7)}° E",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = place.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Hours: ${place.openingHours}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Highlights: ${place.tags.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onToggleFavorite,
                            colors = ButtonDefaults.outlinedButtonColors()
                        ) {
                            Icon(
                                imageVector = if (place.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (place.isFavorite) Color(0xFFF43F5E) else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (place.isFavorite) "Saved" else "Bookmark")
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(onClick = onAddToItinerary) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Schedule Stop")
                        }
                    }
                }
            }
        }
    }
}
