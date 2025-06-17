package com.example.swasth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.swasth.databinding.ActivityDoctorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DoctorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.signupBtn.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val email = binding.email.text.toString()
            val specialization = binding.specialization.text.toString()
            val experience = binding.experience.text.toString()
            val phoneNumber = binding.phonenumber.text.toString()
            val address = binding.address.text.toString()
            val consultationFee = binding.consultationFee.text.toString()
            val pass = binding.passwordEditText.text.toString()
            val confirmPass = binding.confirmPasswordEditText.text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() &&
                specialization.isNotEmpty() && experience.isNotEmpty() && phoneNumber.isNotEmpty() &&
                address.isNotEmpty() && consultationFee.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()
            ) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = firebaseAuth.currentUser?.uid ?: ""
                                val database = FirebaseDatabase.getInstance()
                                val doctorRef = database.getReference("users/doctors/$uid")

                                val doctorData = mapOf(
                                    "firstName" to firstName,
                                    "lastName" to lastName,
                                    "specialization" to specialization,
                                    "experience" to experience,
                                    "rating" to 0.0, // Default rating for new doctors
                                    "imageUrl" to "", // Can be updated later when user uploads profile image
                                    "email" to email,
                                    "phone" to phoneNumber,
                                    "address" to address,
                                    "consultationFee" to consultationFee,
                                    "userType" to "Doctor"
                                )

                                doctorRef.setValue(doctorData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Signup & Data Saved!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this,
                                            "Signup Success, but failed to save data",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                            } else {
                                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}