package com.utadeo.uniconnect.data.model.activity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.utadeo.uniconnect.data.model.navigation.AppScreens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesListScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var activities by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            activities = ActivitiesRepository.getAllActivities()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actividades disponibles") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                activities.isEmpty() -> {
                    Text(
                        text = "No hay actividades registradas aún 💤",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(activities) { activity ->
                            ActivityCard(
                                activity = activity,
                                onClick = {
                                    val id = activity["id"] as? String ?: return@ActivityCard
                                    navController.navigate(
                                        AppScreens.ActivityDetailScreen.createRoute(id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityCard(
    activity: Map<String, Any>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = (activity["title"] ?: "Sin título").toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = (activity["description"] ?: "Sin descripción").toString(),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📅 ${activity["date"] ?: "?"}  🕒 ${activity["time"] ?: "?"}",
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = "📍 ${activity["locationName"] ?: "Sin ubicación"}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
