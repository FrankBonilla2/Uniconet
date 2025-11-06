package com.utadeo.uniconnect.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun loginWithEmailAndPassword(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.Success(result.user!!)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun registerWithEmailAndPassword(
        email: String,
        password: String,
        displayName: String? = null
    ): Result<FirebaseUser> {
        return try {
            // Crear usuario
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            // Actualizar display name si se proporciona
            val user = result.user
            if (user != null && !displayName.isNullOrEmpty()) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdates).await()
            }

            Result.Success(user!!)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun sendEmailVerification(): Result<Boolean> {
        return try {
            currentUser?.sendEmailVerification()?.await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun isEmailVerified(): Boolean {
        return currentUser?.isEmailVerified ?: false
    }
}

// Result wrapper para manejar estados
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}