//package com.example.swasth
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.RadioButton
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.swasth.databinding.ActivityPatientBinding
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
//
//class PatientActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityPatientBinding
//    private lateinit var firebaseAuth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPatientBinding.inflate(layoutInflater)
//
//        enableEdgeToEdge()
//        setContentView(binding.root)
//        firebaseAuth = FirebaseAuth.getInstance()
//
//        binding.signupBtn.setOnClickListener {
//            val firstName = binding.firstName.text.toString()
//            val lastName = binding.lastName.text.toString()
//            val phoneNumber = binding.firstnumber.text.toString()
//            val nid = binding.nationalId.text.toString()
//            val gender = binding.gender.text.toString()
//            val dob = binding.birthdate.text.toString()
//            val doctorId = binding.yourdoctorId.text.toString()
//            val weight = binding.weightEditText.text.toString()
//            val height = binding.heightEditText.text.toString()
//            val bloodType = binding.bloodtypeEditText.text.toString()
//            val email = binding.email.text.toString()
//            val pass = binding.passwordEditText.text.toString()
//            val confirmPass = binding.confirmPasswordEditText.text.toString()
//
//            val selectedId = binding.radioGroup.checkedRadioButtonId
//
//            if (firstName.isNotEmpty() && lastName.isNotEmpty() && phoneNumber.isNotEmpty() &&
//                nid.isNotEmpty() && gender.isNotEmpty() && dob.isNotEmpty() &&
//                doctorId.isNotEmpty() && weight.isNotEmpty() && height.isNotEmpty() &&
//                bloodType.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()
//            ) {
//                if (pass == confirmPass) {
//                    if (pass == confirmPass) {
//                        if (selectedId != -1) {
//                            val selectedRadioButton = findViewById<RadioButton>(selectedId)
//                            val selectedText = selectedRadioButton.text.toString()
//                            Toast.makeText(this, "Selected: $selectedText", Toast.LENGTH_SHORT).show()
//
//                            firebaseAuth.createUserWithEmailAndPassword(email, pass)
//                                .addOnCompleteListener { task ->
//                                    if (task.isSuccessful) {
//                                        val uid = firebaseAuth.currentUser?.uid ?: ""
//                                        val database = FirebaseDatabase.getInstance()
//                                        val patientRef = database.getReference("users/patients/$uid")
//
//                                        val patientData = mapOf(
//                                            "firstName" to firstName,
//                                            "lastName" to lastName,
//                                            "phoneNumber" to phoneNumber,
//                                            "nid" to nid,
//                                            "gender" to gender,
//                                            "dob" to dob,
//                                            "doctorId" to doctorId,
//                                            "weight" to weight,
//                                            "height" to height,
//                                            "bloodType" to bloodType,
//                                            "email" to email,
//                                            "userType" to "Patient",
//                                            "healthStatus" to selectedText
//
//                                        )
//
//                                        patientRef.setValue(patientData)
//                                            .addOnSuccessListener {
//                                                Toast.makeText(this, "Signup & Data Saved!", Toast.LENGTH_SHORT).show()
//                                                val intent = Intent(this, MainActivity::class.java)
//                                                startActivity(intent)
//                                            }
//                                            .addOnFailureListener {
//                                                Toast.makeText(this, "Signup Success, but failed to save data", Toast.LENGTH_SHORT).show()
//                                            }
//
//                                    } else {
//                                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                        } else {
//                            Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } else {
//                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}
//

package com.example.swasth

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.swasth.databinding.ActivityPatientBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PatientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupBtn.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val phoneNumber = binding.firstnumber.text.toString()
            val nid = binding.nationalId.text.toString()
            val gender = binding.gender.text.toString()
            val dob = binding.birthdate.text.toString()
            val yourAge = binding.yourage.text.toString()
            val weight = binding.weightEditText.text.toString()
            val height = binding.heightEditText.text.toString()
            val bloodType = binding.bloodtypeEditText.text.toString()
            val email = binding.email.text.toString()
            val pass = binding.passwordEditText.text.toString()
            val confirmPass = binding.confirmPasswordEditText.text.toString()

            // Get diabetes status
            val diabetesSelectionId = binding.radioGroup1.checkedRadioButtonId
            val hasDiabetes = if (diabetesSelectionId != -1) {
                findViewById<RadioButton>(diabetesSelectionId).text.toString()
            } else {
                ""
            }

            // Get blood pressure status
            val bpSelectionId = binding.radioGroup.checkedRadioButtonId
            val hasBloodPressure = if (bpSelectionId != -1) {
                findViewById<RadioButton>(bpSelectionId).text.toString()
            } else {
                ""
            }

            // Get liver disease status
            val liverSelectionId = binding.radioGroup3.checkedRadioButtonId
            val hasLiverDisease = if (liverSelectionId != -1) {
                findViewById<RadioButton>(liverSelectionId).text.toString()
            } else {
                ""
            }

            // Get pregnancy status
            val pregnancySelectionId = binding.radioGroup4.checkedRadioButtonId
            val pregnancyStatus = if (pregnancySelectionId != -1) {
                findViewById<RadioButton>(pregnancySelectionId).text.toString()
            } else {
                ""
            }

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && phoneNumber.isNotEmpty() &&
                nid.isNotEmpty() && gender.isNotEmpty() && dob.isNotEmpty() &&
                yourAge.isNotEmpty() && weight.isNotEmpty() && height.isNotEmpty() &&
                bloodType.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() &&
                hasDiabetes.isNotEmpty() && hasBloodPressure.isNotEmpty() &&
                hasLiverDisease.isNotEmpty() && pregnancyStatus.isNotEmpty()
            ) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = firebaseAuth.currentUser?.uid ?: ""
                                val database = FirebaseDatabase.getInstance()
                                val patientRef = database.getReference("users/patients/$uid")

                                val patientData = mapOf(
                                    "firstName" to firstName,
                                    "lastName" to lastName,
                                    "phoneNumber" to phoneNumber,
                                    "nid" to nid,
                                    "gender" to gender,
                                    "dob" to dob,
                                    "age" to yourAge,
                                    "weight" to weight,
                                    "height" to height,
                                    "bloodType" to bloodType,
                                    "email" to email,
                                    "userType" to "Patient",
                                    "diabetes" to hasDiabetes,
                                    "bloodPressure" to hasBloodPressure,
                                    "liverDisease" to hasLiverDisease,
                                    "pregnancyStatus" to pregnancyStatus
                                )

                                patientRef.setValue(patientData)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Signup & Data Saved!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Signup Success, but failed to save data", Toast.LENGTH_SHORT).show()
                                    }

                            } else {
                                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}