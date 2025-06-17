package com.example.swasth.model

data class Appointment(
    val appointmentId: String = "",
//    val age: Int,
    val doctorId: String = "",
    val doctorName: String = "",
    val patientName: String = "",
    val status: String = "",
    val timestamp: Long = 0L
)

