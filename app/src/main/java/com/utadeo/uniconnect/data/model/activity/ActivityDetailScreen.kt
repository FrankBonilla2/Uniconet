package com.utadeo.uniconnect.data.model.activity

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    navController: NavController,
    activityId: String
) {
    val scope = rememberCoroutineScope()
    var activityData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(activityId) {
        scope.launch {
            isLoading = true
            activityData = ActivitiesRepository.getActivityById(activityId)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la actividad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                activityData == null -> Text(
                    "No se encontraron los detalles 😕",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> ActivityDetailContent(activityData!!)
            }
        }
    }
}

@Composable
fun ActivityDetailContent(activity: Map<String, Any>) {
    val scrollState = rememberScrollState()
    val lat = activity["locationLat"] as? Double
    val lng = activity["locationLng"] as? Double
    val hasMap = lat != null && lng != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = activity["title"].toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = activity["description"].toString(),
            style = MaterialTheme.typography.bodyLarge
        )

        Divider()

        Text(
            text = "📅 Fecha: ${activity["date"] ?: "?"}\n🕒 Hora: ${activity["time"] ?: "?"}",
            style = MaterialTheme.typography.bodyMedium
        )

        if (activity["hasBudget"] == true) {
            Text(
                text = "💰 Presupuesto: ${activity["budgetAmount"]} COP",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "👤 Creado por: ${activity["creatorName"] ?: "Anónimo"}",
            style = MaterialTheme.typography.bodyMedium
        )

        if (hasMap) {
            val location = LatLng(lat!!, lng!!)
            Text(
                text = "📍 ${activity["locationName"] ?: "Ubicación no especificada"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
            MapPreview(location)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* TODO: acción de unirse o confirmar asistencia */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Unirse a la actividad ✨")
        }
    }
}

@Composable
fun MapPreview(location: LatLng) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = location),
                title = "Ubicación"
            )
        }
    }
}
