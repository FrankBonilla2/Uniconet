package com.utadeo.uniconnect.data.model.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.utadeo.uniconnect.data.model.activity.MapPickerScreen
import  com.utadeo.uniconnect.data.model.ui.activity.ActivityFlowScreen
import com.utadeo.uniconnect.data.model.activity.ActivityDetailScreen
import com.utadeo.uniconnect.data.model.activity.ActivitiesListScreen
import com.utadeo.uniconnect.data.model.ui.Login.*
import com.utadeo.uniconnect.data.model.ui.Login.Register.InterestSelectionScreen
import com.utadeo.uniconnect.data.model.ui.Login.Register.MessageTrustScreen
import com.utadeo.uniconnect.data.model.ui.Login.Register.RegistroCompletadoScreen
import com.utadeo.uniconnect.data.model.ui.Login.Register.UserBioScreen
import com.utadeo.uniconnect.data.model.ui.Register.*
import com.utadeo.uniconnect.data.model.ui.activity.*
import com.utadeo.uniconnect.ui.ChatScreen
import com.utadeo.uniconnect.ui.splash.SplashScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.SplashScreen.route
    ) {
        // 🟢 Pantalla inicial (Splash)
        composable(AppScreens.SplashScreen.route) {
            SplashScreen(navController)
        }

        // 🟠 Pantalla de inicio (Login o registro)
        composable(AppScreens.LoginScreen.route) {
            LoginAndRegister(navController)
        }

        // 🟣 Pantalla de registro
        composable(AppScreens.RegisterScreen.route) {
            RegisterScreen(navController)
        }

        // 🔐 Registro con contraseña
        composable(
            route = AppScreens.RegisterWithPasswordScreen.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            RegisterWithPasswordScreen(navController, email)
        }

        // 🔑 Login con contraseña
        composable(AppScreens.LoginWithPasswordScreen.route) {
            LoginWithPassword(navController)
        }

        // 🏠 Pantalla principal
        composable(AppScreens.HomeScreen.route) {
            HomeScreen(navController)
        }

        // 💬 Pantalla del chat (con argumento chatId)
        composable(
            route = AppScreens.ChatScreen.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatScreen(navController, chatId)
        }

        // ⚙️ Pantalla de configuración
        composable(AppScreens.SettingsScreen.route) {
            SettingsScreen(navController)
        }

        // 📸 Pantalla de foto de perfil
        composable(AppScreens.ProfilePictureScreen.route) {
            ProfilePictureScreen(navController)
        }

        // 🪪 Pantalla de verificación de identidad (corregido)
        composable(AppScreens.IdentityVerification.route) {
            IdentityVerification(navController)
        }

        // 🟡 --- Pantallas de Actividades ---
        composable(AppScreens.ActivityFlowScreen.route) {
            ActivityFlowScreen(navController)
        }

        composable(route = AppScreens.QuestionParticipateScreen.route) {
            QuestionParticipateScreen(navController)
        }


        // 🟦 --- Pantalla para crear pregunta ---
        composable(AppScreens.QuestionCreateScreen.route) {
            QuestionCreateScreen(navController)
        }

        composable(AppScreens.MessageTrust.route) {
            MessageTrustScreen(navController)
        }

        composable(AppScreens.ProfileCreationScreen.route) {
            ProfileCreationScreen(navController)
        }

        composable(AppScreens.UserBioScreen.route) {
            UserBioScreen(navController)
        }

        composable(AppScreens.InterestSelectionScreen.route) {
            InterestSelectionScreen(navController)
        }

        composable(AppScreens.RegistroCompletadoScreen.route) {
            RegistroCompletadoScreen(navController)
        }

        composable(AppScreens.ActivityCreateScreen.route) {
            ActivityCreateScreen(navController)
        }

        composable("mapPicker") {
            MapPickerScreen(navController = navController)
        }
        composable(AppScreens.ActivitiesListScreen.route) {
            ActivitiesListScreen(navController)
        }

        composable(
            route = AppScreens.ActivityDetailScreen.route,
            arguments = listOf(navArgument("activityId") { type = NavType.StringType })
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
            ActivityDetailScreen(navController, activityId)
        }


    }
}
