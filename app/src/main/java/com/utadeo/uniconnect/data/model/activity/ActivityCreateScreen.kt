package com.utadeo.uniconnect.data.model.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.utadeo.uniconnect.R
import com.utadeo.uniconnect.data.model.navigation.AppScreens
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCreateScreen(navController: NavController) {
    val poppins = FontFamily(Font(R.font.poppins_regular))

    // ✅ Recibir datos desde el mapa
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    var selectedLocation by remember { mutableStateOf("") }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getLiveData<String>("selected_address")?.observeForever { address ->
            selectedLocation = address
        }
        savedStateHandle?.getLiveData<LatLng>("selected_latlng")?.observeForever { latLng ->
            selectedLatLng = latLng
        }
    }

    // Estados del formulario
    var activityTitle by remember { mutableStateOf("") }
    var activityDescription by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedHour by remember { mutableStateOf(12) }
    var selectedMinute by remember { mutableStateOf(0) }
    var hasBudget by remember { mutableStateOf<Boolean?>(null) }
    var budgetAmount by remember { mutableStateOf("") }

    // Estados para Date/Time Pickers
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Validaciones
    val maxTitleChars = 50
    val maxDescChars = 150
    val isFormValid = activityTitle.isNotBlank() &&
            activityDescription.length >= 10 &&
            hasBudget != null &&
            (hasBudget == false || budgetAmount.isNotBlank()) &&
            selectedLocation.isNotBlank()

    // Date Picker Dialog
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Calendar.getInstance().timeInMillis
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = millis
                        }
                        selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
                        selectedMonth = calendar.get(Calendar.MONTH) + 1
                        selectedYear = calendar.get(Calendar.YEAR)
                    }
                    showDatePicker = false
                }) {
                    Text("OK", fontFamily = poppins)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", fontFamily = poppins)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = false
    )

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    showTimePicker = false
                }) {
                    Text("OK", fontFamily = poppins)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar", fontFamily = poppins)
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDD835))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .padding(bottom = 100.dp)
        ) {
            // Header mejorado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigate(AppScreens.ActivityFlowScreen.route) },
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título con emoji
            Text(
                text = "Crea tu actividad",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = poppins,
                color = Color.Black,
                lineHeight = 38.sp
            )

            Text(
                text = "Comparte algo increíble con la comunidad",
                fontSize = 14.sp,
                fontFamily = poppins,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card para título
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Título de la actividad",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppins,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = activityTitle,
                        onValueChange = { if (it.length <= maxTitleChars) activityTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = {
                            Text("Ej: Partido de fútbol", color = Color.Gray, fontFamily = poppins)
                        },
                        textStyle = LocalTextStyle.current.copy(fontFamily = poppins, fontSize = 16.sp),
                        singleLine = true
                    )
                    Text(
                        text = "${activityTitle.length}/$maxTitleChars",
                        fontSize = 11.sp,
                        fontFamily = poppins,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card para descripción
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Descripción",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppins,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = activityDescription,
                        onValueChange = { if (it.length <= maxDescChars) activityDescription = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = {
                            Text("Cuenta más detalles...", color = Color.Gray, fontFamily = poppins)
                        },
                        textStyle = LocalTextStyle.current.copy(fontFamily = poppins, fontSize = 14.sp),
                        maxLines = 5
                    )
                    Text(
                        text = "${activityDescription.length}/$maxDescChars",
                        fontSize = 11.sp,
                        fontFamily = poppins,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card para fecha y hora
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fecha y hora",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppins,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón de fecha
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF5F5F5))
                                .border(1.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .clickable { showDatePicker = true }
                                .padding(12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "${selectedDay.toString().padStart(2, '0')}/${selectedMonth.toString().padStart(2, '0')}/$selectedYear",
                                    fontFamily = poppins,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }

                        // Botón de hora
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF5F5F5))
                                .border(1.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .clickable { showTimePicker = true }
                                .padding(12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                val period = if (selectedHour < 12) "AM" else "PM"
                                val displayHour = when {
                                    selectedHour == 0 -> 12
                                    selectedHour > 12 -> selectedHour - 12
                                    else -> selectedHour
                                }
                                Text(
                                    text = "${displayHour}:${selectedMinute.toString().padStart(2, '0')} $period",
                                    fontFamily = poppins,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card para presupuesto
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "¿Tiene presupuesto?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppins,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BudgetChip(
                            text = "Sí",
                            isSelected = hasBudget == true,
                            onClick = { hasBudget = true },
                            modifier = Modifier.weight(1f),
                            poppins = poppins
                        )
                        BudgetChip(
                            text = "No",
                            isSelected = hasBudget == false,
                            onClick = {
                                hasBudget = false
                                budgetAmount = ""
                            },
                            modifier = Modifier.weight(1f),
                            poppins = poppins
                        )
                    }

                    if (hasBudget == true) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = budgetAmount,
                            onValueChange = { budgetAmount = it.filter { char -> char.isDigit() } },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = {
                                Text("$0", color = Color.Gray, fontFamily = poppins)
                            },
                            leadingIcon = {
                                Text("$", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                fontFamily = poppins,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card para ubicación
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ubicación",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppins,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5F5F5))
                            .border(2.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .clickable { navController.navigate("mapPicker") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedLocation.isBlank()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.Black.copy(alpha = 0.4f),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Toca para seleccionar",
                                    fontSize = 14.sp,
                                    fontFamily = poppins,
                                    color = Color.Black.copy(alpha = 0.5f)
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFFE91E63),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = selectedLocation,
                                    fontSize = 13.sp,
                                    fontFamily = poppins,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Botón publicar mejorado
        Button(
            onClick = {
                if (isFormValid) {
                    // TODO: Publicar en Firebase
                    // Luego navegar a HomeScreen
                    navController.navigate(AppScreens.HomeScreen.route) {
                        popUpTo(AppScreens.ActivityFlowScreen.route) { inclusive = true }
                    }
                }
            },
            enabled = isFormValid,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp)
                .height(56.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) Color.Black else Color.Gray,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Publicar actividad",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = poppins,
                color = if (isFormValid) Color(0xFFFDD835) else Color.White
            )
        }
    }
}

@Composable
private fun BudgetChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    poppins: FontFamily
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color.Black else Color(0xFFF5F5F5))
            .border(
                1.dp,
                if (isSelected) Color.Black else Color.Black.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = poppins,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.6f)
        )
    }
}