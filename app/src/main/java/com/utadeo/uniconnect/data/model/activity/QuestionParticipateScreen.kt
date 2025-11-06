package com.utadeo.uniconnect.data.model.ui.activity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.utadeo.uniconnect.R
import com.utadeo.uniconnect.data.model.activity.QuestionsRepository
import com.utadeo.uniconnect.data.model.navigation.AppScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel
@HiltViewModel
class QuestionParticipateViewModel @Inject constructor() : ViewModel() {

    private val questionsRepository = QuestionsRepository
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var preguntas by mutableStateOf<List<Pregunta>>(emptyList())
        private set

    var haRespondido by mutableStateOf(false)
        private set

    init {
        cargarPreguntas()
    }

    fun cargarPreguntas() {
        viewModelScope.launch {
            try {
                val datos = questionsRepository.getQuestions()
                preguntas = datos.map { map ->
                    Pregunta(
                        id = map["id"] as? String ?: "",
                        usuario = map["userName"] as? String ?: "Anónimo",
                        texto = map["text"] as? String ?: "",
                        respuestas = mutableListOf(),
                        numRespuestas = 0
                    )
                }
                // Cargar número de respuestas para cada pregunta
                preguntas.forEach { pregunta ->
                    cargarNumeroRespuestas(pregunta)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun cargarNumeroRespuestas(pregunta: Pregunta) {
        viewModelScope.launch {
            try {
                if (pregunta.id.isBlank()) return@launch
                val datos = questionsRepository.getResponses(pregunta.id)
                pregunta.numRespuestas = datos.size
                preguntas = preguntas.map {
                    if (it.id == pregunta.id) pregunta else it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchResponsesSync(questionId: String): List<Map<String, Any>> {
        return try {
            questionsRepository.getResponses(questionId)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun agregarRespuesta(pregunta: Pregunta, texto: String, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                if (pregunta.id.isBlank()) return@launch
                questionsRepository.addResponse(pregunta.id, texto)
                haRespondido = true
                cargarNumeroRespuestas(pregunta)
                onDone?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getUsuarioActual(): String {
        val user = auth.currentUser
        return user?.displayName ?: user?.email ?: "Tú"
    }
}

// Modelos
data class Respuesta(
    val usuario: String,
    val texto: String
)

data class Pregunta(
    val id: String,
    val usuario: String,
    val texto: String,
    val respuestas: MutableList<Respuesta> = mutableListOf(),
    var numRespuestas: Int = 0
)

// Screen Principal
@Composable
fun QuestionParticipateScreen(
    navController: NavController,
    viewModel: QuestionParticipateViewModel = hiltViewModel()
) {
    val poppins = FontFamily(Font(R.font.poppins_regular))
    val preguntas = viewModel.preguntas
    val usuarioActual = viewModel.getUsuarioActual()
    val haRespondido = viewModel.haRespondido

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDD835))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Responde preguntas",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = poppins,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo con indicación
            Text(
                text = if (haRespondido)
                    "✓ Ya respondiste una pregunta"
                else
                    "Responde al menos una pregunta para continuar",
                fontSize = 14.sp,
                fontFamily = poppins,
                color = if (haRespondido) Color(0xFF4CAF50) else Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 56.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de preguntas
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (preguntas.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "📭",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No hay preguntas disponibles",
                                    fontSize = 16.sp,
                                    fontFamily = poppins,
                                    color = Color.Black.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(preguntas) { pregunta ->
                        PreguntaCard(
                            pregunta = pregunta,
                            usuarioActual = usuarioActual,
                            viewModel = viewModel,
                            poppins = poppins
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Botón flotante de continuar (solo si ha respondido)
        if (haRespondido) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(28.dp)
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .clickable {
                        navController.navigate(AppScreens.HomeScreen.route) {
                            popUpTo(AppScreens.ActivityFlowScreen.route) { inclusive = true }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Continuar",
                    tint = Color(0xFFFDD835),
                    modifier = Modifier.size(36.dp)
                )
            }
        } else {
            // Mensaje de advertencia si no ha respondido
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(28.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.9f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚠️ Responde al menos una pregunta para poder continuar",
                    fontSize = 14.sp,
                    fontFamily = poppins,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PreguntaCard(
    pregunta: Pregunta,
    usuarioActual: String,
    viewModel: QuestionParticipateViewModel,
    poppins: FontFamily
) {
    var mostrarRespuestas by remember { mutableStateOf(false) }
    var mostrarCampoRespuesta by remember { mutableStateOf(false) }
    var nuevaRespuesta by remember { mutableStateOf("") }
    var cargandoRespuestas by remember { mutableStateOf(false) }

    // Cargar respuestas cuando se despliega
    LaunchedEffect(key1 = mostrarRespuestas) {
        if (mostrarRespuestas && pregunta.respuestas.isEmpty()) {
            cargandoRespuestas = true
            val datos = viewModel.fetchResponsesSync(pregunta.id)
            pregunta.respuestas.clear()
            pregunta.respuestas.addAll(
                datos.map { rmap ->
                    Respuesta(
                        usuario = rmap["userName"] as? String ?: rmap["user"] as? String ?: "Anónimo",
                        texto = rmap["text"] as? String ?: rmap["answerText"] as? String ?: ""
                    )
                }
            )
            cargandoRespuestas = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Usuario
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = pregunta.usuario,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = poppins,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto de la pregunta
            Text(
                text = pregunta.texto,
                fontSize = 16.sp,
                fontFamily = poppins,
                color = Color.Black,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón de ver respuestas con número
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { mostrarRespuestas = !mostrarRespuestas }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Respuestas",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${pregunta.numRespuestas}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppins,
                        color = Color.Black
                    )
                }

                // Botón Responder
                Button(
                    onClick = { mostrarCampoRespuesta = !mostrarCampoRespuesta },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Responder",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppins
                    )
                }
            }

            // Sección de respuestas expandible
            AnimatedVisibility(
                visible = mostrarRespuestas,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (cargandoRespuestas) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Cargando respuestas...",
                                fontSize = 14.sp,
                                fontFamily = poppins,
                                color = Color.Gray
                            )
                        }
                    } else if (pregunta.respuestas.isNotEmpty()) {
                        pregunta.respuestas.forEach { respuesta ->
                            RespuestaItem(respuesta, poppins)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    } else {
                        Text(
                            "Aún no hay respuestas. ¡Sé el primero en responder!",
                            fontSize = 14.sp,
                            fontFamily = poppins,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Campo de respuesta expandible
            AnimatedVisibility(
                visible = mostrarCampoRespuesta,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nuevaRespuesta,
                        onValueChange = { nuevaRespuesta = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Escribe tu respuesta...",
                                fontFamily = poppins,
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.LightGray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp,
                            fontFamily = poppins
                        ),
                        minLines = 2,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            nuevaRespuesta = ""
                            mostrarCampoRespuesta = false
                        }) {
                            Text(
                                "Cancelar",
                                fontFamily = poppins,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (nuevaRespuesta.isNotBlank()) {
                                    viewModel.agregarRespuesta(pregunta, nuevaRespuesta) {
                                        nuevaRespuesta = ""
                                        mostrarCampoRespuesta = false
                                        mostrarRespuestas = true
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                disabledContainerColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = nuevaRespuesta.isNotBlank()
                        ) {
                            Text(
                                "Enviar",
                                color = Color.White,
                                fontFamily = poppins,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RespuestaItem(respuesta: Respuesta, poppins: FontFamily) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = respuesta.usuario,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = poppins,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = respuesta.texto,
                fontSize = 14.sp,
                fontFamily = poppins,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )
        }
    }
}