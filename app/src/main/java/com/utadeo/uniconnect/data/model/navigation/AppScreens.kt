package com.utadeo.uniconnect.data.model.navigation

// Clase sellada que define todas las rutas de navegación de la app
sealed class AppScreens(val route: String) {

    // 🟢 Pantalla inicial / Splash
    object SplashScreen : AppScreens("splash_screen")

    // 🟠 Autenticación y registro
    object LoginScreen : AppScreens("login_screen")
    object LoginWithPasswordScreen : AppScreens("login_with_password_screen")
    object RegisterScreen : AppScreens("register_screen")

    object RegisterWithPasswordScreen : AppScreens("register_with_password_screen/{email}") {
        fun createRoute(email: String): String = "register_with_password_screen/$email"
    }

    // 🏠 Pantalla principal (Home)
    object HomeScreen : AppScreens("home_screen")

    // ⚙️ Configuración y perfil
    object SettingsScreen : AppScreens("settings_screen")
    object ProfilePictureScreen : AppScreens("profile_picture_screen")
    object ProfileCreationScreen : AppScreens("profile_creation_screen")
    object IdentityVerification : AppScreens("identity_verification")

    // 💬 Chat individual
    object ChatScreen : AppScreens("chat_screen/{chatId}") {
        fun createRoute(chatId: String): String = "chat_screen/$chatId"
    }

    // 🟡 Flujo de actividades
    object ActivityFlowScreen : AppScreens("activity_flow_screen")

    object ActivityStepScreen : AppScreens("activity_step_screen/{type}") {
        fun createRoute(type: String): String = "activity_step_screen/$type"
    }

    // 🟦 Creación de preguntas
    object QuestionCreateScreen : AppScreens("question_create_screen")

    object QuestionParticipateScreen : AppScreens("question_participate_screen")

    object MessageTrust: AppScreens("message_trust")

    object UserBioScreen: AppScreens("user_bio_screen")

    object InterestSelectionScreen: AppScreens("interest_selection_screen")

    object RegistroCompletadoScreen: AppScreens ("registro_completado_screen")

    object ActivityCreateScreen: AppScreens("activity_create_screen")

    object MapPickerScreen: AppScreens("map_picker_screen")

    object ActivitiesListScreen : AppScreens("activities_list_screen")

    object ActivityDetailScreen : AppScreens("activity_detail_screen/{activityId}") {
        fun createRoute(activityId: String) = "activity_detail_screen/$activityId"
    }





}
