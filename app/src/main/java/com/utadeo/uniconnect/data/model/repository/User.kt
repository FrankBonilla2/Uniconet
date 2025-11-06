package com.utadeo.uniconnect.data.model.repository

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val profilePicture: String = "",
    val interests: List<String> = emptyList(),
    val university: String = "",
    val career: String = "",
    val semester: Int = 0,
    val isEmailVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)