package com.utadeo.uniconnect.data.model.activity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object QuestionsRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // 📚 Nombre de la colección principal y subcolección
    private const val COLLECTION_NAME = "questions"
    private const val RESPONSES_SUBCOL = "responses"

    /**
     * 🟨 Guarda una nueva pregunta en Firestore con el nombre del usuario autenticado.
     */
    suspend fun addQuestion(questionText: String) {
        val currentUser = auth.currentUser
        val userName = currentUser?.displayName ?: currentUser?.email ?: "Usuario anónimo"

        if (questionText.isNotBlank()) {
            val questionData = mapOf(
                "text" to questionText.trim(),
                "userName" to userName,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection(COLLECTION_NAME).add(questionData).await()
        }
    }

    /**
     * 🟦 Obtiene todas las preguntas guardadas en Firestore, ordenadas por fecha.
     * Incluye el ID de cada documento para poder agregar respuestas.
     */
    suspend fun getQuestions(): List<Map<String, Any>> {
        val snapshot = firestore.collection(COLLECTION_NAME)
            .orderBy("timestamp")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            val mapWithId: MutableMap<String, Any> = HashMap(data)
            mapWithId["id"] = doc.id
            mapWithId
        }
    }

    /**
     * 🟩 Agrega una respuesta (con el nombre del usuario autenticado)
     * a la subcolección "responses" dentro de una pregunta específica.
     */
    suspend fun addResponse(questionId: String, responseText: String) {
        val currentUser = auth.currentUser
        val userName = currentUser?.displayName ?: currentUser?.email ?: "Usuario anónimo"

        if (responseText.isNotBlank()) {
            val responseData = mapOf(
                "text" to responseText.trim(),
                "userName" to userName,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection(COLLECTION_NAME)
                .document(questionId)
                .collection(RESPONSES_SUBCOL)
                .add(responseData)
                .await()
        }
    }

    /**
     * 🟧 Obtiene todas las respuestas de una pregunta (subcolección "responses"),
     * ordenadas por fecha y con el ID de cada documento incluido.
     */
    suspend fun getResponses(questionId: String): List<Map<String, Any>> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .document(questionId)
                .collection(RESPONSES_SUBCOL)
                .orderBy("timestamp")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                val mapWithId: MutableMap<String, Any> = HashMap(data)
                mapWithId["id"] = doc.id
                mapWithId
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
