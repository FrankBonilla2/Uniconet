package com.utadeo.uniconnect.data.model.activity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * 🔥 Repositorio central para manejar actividades en Firestore.
 * Guarda, obtiene y administra actividades creadas por los usuarios.
 */
object ActivitiesRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private const val COLLECTION_NAME = "activities"

    /**
     * 🟢 Crea una nueva actividad en Firestore
     */
    suspend fun createActivity(
        title: String,
        description: String,
        date: String,
        time: String,
        hasBudget: Boolean,
        budgetAmount: Int?,
        locationLat: Double?,
        locationLng: Double?,
        locationName: String?
    ): Boolean {
        val currentUser = auth.currentUser
        val creatorName = currentUser?.displayName ?: currentUser?.email ?: "Usuario Anónimo"
        val creatorId = currentUser?.uid ?: "unknown_user"

        val data = hashMapOf(
            "title" to title,
            "description" to description,
            "date" to date,
            "time" to time,
            "hasBudget" to hasBudget,
            "budgetAmount" to (budgetAmount ?: 0),
            "locationLat" to locationLat,
            "locationLng" to locationLng,
            "locationName" to locationName,
            "creatorName" to creatorName,
            "creatorId" to creatorId,
            "timestamp" to System.currentTimeMillis()
        )

        return try {
            firestore.collection(COLLECTION_NAME).add(data).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 🔵 Obtiene todas las actividades de Firestore (ordenadas por fecha de creación)
     */
    suspend fun getAllActivities(): List<Map<String, Any>> {
        return try {
            val snapshot = firestore.collection(COLLECTION_NAME)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                val mapWithId = HashMap(data)
                mapWithId["id"] = doc.id
                mapWithId
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 🟣 Obtiene una actividad específica por su ID
     */
    suspend fun getActivityById(activityId: String): Map<String, Any>? {
        return try {
            val doc = firestore.collection(COLLECTION_NAME).document(activityId).get().await()
            doc.data?.plus("id" to doc.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
