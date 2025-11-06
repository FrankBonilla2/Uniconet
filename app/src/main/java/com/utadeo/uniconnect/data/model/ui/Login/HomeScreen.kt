package com.utadeo.uniconnect.data.model.ui.Login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.utadeo.uniconnect.R
import com.utadeo.uniconnect.data.model.navigation.AppScreens
import com.utadeo.uniconnect.ui.theme.primary_color
import com.utadeo.uniconnect.ui.theme.white
import com.utadeo.uniconnect.ui.theme.black
import java.text.SimpleDateFormat
import java.util.*

data class Message(
    val id: String,
    val sender: String,
    val text: String,
    val timestamp: Long
)

data class ChatPreview(
    val id: String,
    val name: String,
    val messages: List<Message>,
    val unreadCount: Int,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val chats = listOf(
        ChatPreview(
            id = "chat_1",
            name = "Jhon",
            messages = listOf(
                Message("1", "Jhon", "Bienvenido a UniConnect 🎉", 1690000000000),
                Message("2", "Yo", "Hola Jhon, gracias!", 1690000100000)
            ),
            unreadCount = 1,
            imageUrl = "https://i.pravatar.cc/300?img=1"
        ),
        ChatPreview(
            id = "chat_2",
            name = "Maria",
            messages = listOf(
                Message("3", "Maria", "¿Cómo estás?", 1690000200000)
            ),
            unreadCount = 0,
            imageUrl = "https://i.pravatar.cc/300?img=2"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primary_color),
                title = {},
                actions = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { /* Futuro perfil */ },
                        tint = black
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = { navController.navigate(AppScreens.SettingsScreen.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = black)
                    }
                    IconButton(onClick = { /* Buscar chats */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = black)
                    }
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = black)
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color(0xFFF1F1F1)) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = black)
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { navController.navigate(AppScreens.ActivityFlowScreen.route) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { /* Ir al perfil (futuro) */ }) {
                    Icon(Icons.Default.Person, contentDescription = "Perfil", tint = black)
                }
            }
        },
        containerColor = Color(0xFFF2F2F2)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(chats) { chat ->
                    ChatCard(chat) {
                        navController.navigate("chat_screen/${chat.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatCard(chat: ChatPreview, onClick: () -> Unit) {
    val lastMessage = chat.messages.lastOrNull()?.text ?: "Sin mensajes aún"
    val lastTime = chat.messages.lastOrNull()?.timestamp?.let { timeLong ->
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timeLong))
    } ?: ""

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .background(white)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(chat.imageUrl),
                contentDescription = chat.name,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.name,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = lastMessage,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                if (chat.unreadCount > 0) {
                    Surface(
                        color = Color.Red,
                        shape = CircleShape,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = chat.unreadCount.toString(),
                                color = white,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(lastTime, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    // val navController = rememberNavController()
    // HomeScreen(navController)
}
