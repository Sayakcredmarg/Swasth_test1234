package com.example.swasth.model

data class Doctor(
    val firstName: String = "",
    val lastName: String = "",
    val specialization: String = "",
    val experience: String = "",
    val rating: Float = 0.0f,
    val imageUrl: String = "",
    val id: String = "" // Firestore document ID
) {
    val fullName: String
        get() = "Dr. ${firstName.trim()} ${lastName.trim()}"
}
