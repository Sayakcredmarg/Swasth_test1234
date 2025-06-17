package com.example.swasth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.swasth.EditProfileActivity
import com.example.swasth.MainActivity
import com.example.swasth.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserFragment : Fragment() {

    private lateinit var welcomeTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var dobTextView: TextView
    private lateinit var bloodTextView: TextView
    private lateinit var heightTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var nidTextView: TextView
    private lateinit var userTypeTextView: TextView
    private lateinit var ageTextView: TextView

    // Specific health data fields
    private lateinit var diabetesTextView: TextView
    private lateinit var bloodPressureTextView: TextView
    private lateinit var liverDiseaseTextView: TextView
    private lateinit var pregnancyStatusTextView: TextView

    // Edit profile button
    private lateinit var editProfileButton: Button
    private lateinit var logoutButton: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var userId: String
    private val userType = "Patient"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment (patient layout)
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        // Initialize UI elements
        initializeViews(view)

        logoutButton = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logoutUser()
        }

        // Set up click listener for edit profile button
        editProfileButton.setOnClickListener {
            navigateToEditProfile()
        }

        // Get current user and load data
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            email?.let { findPatientByEmail(it) }
        } else {
            // Not logged in, show message
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initializeViews(view: View) {
        welcomeTextView = view.findViewById(R.id.welcomeTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        phoneTextView = view.findViewById(R.id.phoneTextView)
        genderTextView = view.findViewById(R.id.genderTextView)
        dobTextView = view.findViewById(R.id.dobTextView)
        bloodTextView = view.findViewById(R.id.bloodTextView)
        heightTextView = view.findViewById(R.id.heightTextView)
        weightTextView = view.findViewById(R.id.weightTextView)
        nidTextView = view.findViewById(R.id.nidTextView)
        userTypeTextView = view.findViewById(R.id.userTypeTextView)
        ageTextView = view.findViewById(R.id.ageTextView)

        // Initialize health data views
        diabetesTextView = view.findViewById(R.id.diabetesTextView)
        bloodPressureTextView = view.findViewById(R.id.bloodPressureTextView)
        liverDiseaseTextView = view.findViewById(R.id.liverDiseaseTextView)
        pregnancyStatusTextView = view.findViewById(R.id.pregnancyStatusTextView)

        // Initialize edit profile button
        editProfileButton = view.findViewById(R.id.editProfileButton)
    }

    private fun findPatientByEmail(email: String) {
        val patientsRef = mDatabase.child("users").child("patients")
        patientsRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User is a patient
                    for (patientSnapshot in dataSnapshot.children) {
                        userId = patientSnapshot.key ?: ""
                        loadPatientData()
                        return
                    }
                } else {
                    // Patient not found
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Patient profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun loadPatientData() {
        val patientRef = mDatabase.child("users").child("patients").child(userId)
        patientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && isAdded) {
                    // Get patient data
                    val firstName = dataSnapshot.child("firstName").getValue(String::class.java) ?: ""
                    val lastName = dataSnapshot.child("lastName").getValue(String::class.java) ?: ""
                    val fullName = "$firstName $lastName"

                    // Set welcome text with actual name
                    welcomeTextView.text = "Welcome, $fullName!"

                    // Set other fields
                    emailTextView.text = "Email: ${dataSnapshot.child("email").getValue(String::class.java) ?: ""}"
                    phoneTextView.text = "Phone: ${dataSnapshot.child("phoneNumber").getValue(String::class.java) ?: ""}"
                    genderTextView.text = "Gender: ${dataSnapshot.child("gender").getValue(String::class.java) ?: ""}"
                    dobTextView.text = "Date of Birth: ${dataSnapshot.child("dob").getValue(String::class.java) ?: ""}"
                    bloodTextView.text = "Blood Type: ${dataSnapshot.child("bloodType").getValue(String::class.java) ?: ""}"
                    heightTextView.text = "Height: ${dataSnapshot.child("height").getValue(String::class.java) ?: ""}"
                    weightTextView.text = "Weight: ${dataSnapshot.child("weight").getValue(String::class.java) ?: ""}"
                    nidTextView.text = "National ID: ${dataSnapshot.child("nid").getValue(String::class.java) ?: ""}"
                    userTypeTextView.text = "User Type: $userType"
                    ageTextView.text = "Age: ${dataSnapshot.child("age").getValue(String::class.java) ?: ""}"

                    // Set health data fields
                    diabetesTextView.text = "Diabetes: ${dataSnapshot.child("diabetes").getValue(String::class.java) ?: "N/A"}"
                    bloodPressureTextView.text = "Blood Pressure: ${dataSnapshot.child("bloodPressure").getValue(String::class.java) ?: "N/A"}"
                    liverDiseaseTextView.text = "Liver Disease: ${dataSnapshot.child("liverDisease").getValue(String::class.java) ?: "N/A"}"
                    pregnancyStatusTextView.text = "Pregnancy Status: ${dataSnapshot.child("pregnancyStatus").getValue(String::class.java) ?: "N/A"}"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun navigateToEditProfile() {
        // Create an intent to start the EditProfileActivity
        val intent = Intent(requireContext(), EditProfileActivity::class.java).apply {
            // Pass necessary data to edit profile activity
            putExtra("USER_ID", userId)
            putExtra("USER_TYPE", userType)
        }
        startActivity(intent)
    }

    private fun logoutUser() {
        mAuth.signOut()
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        // Redirect to login screen
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}


//package com.example.swasth.fragments
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import com.example.swasth.EditProfileActivity
//import com.example.swasth.MainActivity
//import com.example.swasth.R
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//class UserFragment : Fragment() {
//
//    private lateinit var welcomeTextView: TextView
//    private lateinit var emailTextView: TextView
//    private lateinit var phoneTextView: TextView
//    private lateinit var genderTextView: TextView
//    private lateinit var dobTextView: TextView
//    private lateinit var bloodTextView: TextView
//    private lateinit var heightTextView: TextView
//    private lateinit var weightTextView: TextView
//    private lateinit var nidTextView: TextView
//    private lateinit var userTypeTextView: TextView
//    private lateinit var doctorIdTextView: TextView
//
//    // Specific health data fields
//    private lateinit var diabetesTextView: TextView
//    private lateinit var bloodPressureTextView: TextView
//    private lateinit var liverDiseaseTextView: TextView
//    private lateinit var pregnancyStatusTextView: TextView
//
//    // Edit profile button
//    private lateinit var editProfileButton: Button
//    private lateinit var logoutButton: Button
//    private lateinit var mAuth: FirebaseAuth
//    private lateinit var mDatabase: DatabaseReference
//    private lateinit var userId: String
//    private lateinit var userType: String
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_user, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Initialize Firebase Auth and Database
//        mAuth = FirebaseAuth.getInstance()
//        mDatabase = FirebaseDatabase.getInstance().reference
//
//        // Initialize UI elements
//        initializeViews(view)
//        logoutButton = view.findViewById(R.id.logoutButton)
//        logoutButton.setOnClickListener {
//            logoutUser()
//        }
//        // Set up click listener for edit profile button
//        editProfileButton.setOnClickListener {
//            navigateToEditProfile()
//        }
//
//        // Get current user and load data
//        val currentUser = mAuth.currentUser
//        if (currentUser != null) {
//            val email = currentUser.email
//
//            // First, determine if user is a patient or doctor based on email
//            email?.let { determineUserType(it) }
//        } else {
//            // Not logged in, show message
//            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show()
//            // Cannot call finish() in a fragment, instead you might want to navigate back
//            requireActivity().supportFragmentManager.popBackStack()
//        }
//    }
//
//    private fun initializeViews(view: View) {
//        welcomeTextView = view.findViewById(R.id.welcomeTextView)
//        emailTextView = view.findViewById(R.id.emailTextView)
//        phoneTextView = view.findViewById(R.id.phoneTextView)
//        genderTextView = view.findViewById(R.id.genderTextView)
//        dobTextView = view.findViewById(R.id.dobTextView)
//        bloodTextView = view.findViewById(R.id.bloodTextView)
//        heightTextView = view.findViewById(R.id.heightTextView)
//        weightTextView = view.findViewById(R.id.weightTextView)
//        nidTextView = view.findViewById(R.id.nidTextView)
//        userTypeTextView = view.findViewById(R.id.userTypeTextView)
//        doctorIdTextView = view.findViewById(R.id.doctorIdTextView)
//
//
//        // Initialize health data views
//        diabetesTextView = view.findViewById(R.id.diabetesTextView)
//        bloodPressureTextView = view.findViewById(R.id.bloodPressureTextView)
//        liverDiseaseTextView = view.findViewById(R.id.liverDiseaseTextView)
//        pregnancyStatusTextView = view.findViewById(R.id.pregnancyStatusTextView)
//
//        // Initialize edit profile button
//        editProfileButton = view.findViewById(R.id.editProfileButton)
//    }
//
//    private fun determineUserType(email: String) {
//        // Check in doctors first
//        val doctorsRef = mDatabase.child("users").child("doctors")
//        doctorsRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // User is a doctor
//                    userType = "Doctor"
//                    for (doctorSnapshot in dataSnapshot.children) {
//                        userId = doctorSnapshot.key ?: ""
//                        loadDoctorData()
//                        return
//                    }
//                } else {
//                    // Not found in doctors, check patients
//                    checkInPatients(email)
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                if (isAdded) {
//                    Toast.makeText(requireContext(), "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
//    }
//
//    private fun checkInPatients(email: String) {
//        val patientsRef = mDatabase.child("users").child("patients")
//        patientsRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // User is a patient
//                    userType = "Patient"
//                    for (patientSnapshot in dataSnapshot.children) {
//                        userId = patientSnapshot.key ?: ""
//                        loadPatientData()
//                        return
//                    }
//                } else {
//                    // User not found in either collection
//                    if (isAdded) {
//                        Toast.makeText(requireContext(), "User profile not found", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                if (isAdded) {
//                    Toast.makeText(requireContext(), "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
//    }
//
//    private fun loadDoctorData() {
//        val doctorRef = mDatabase.child("users").child("doctors").child(userId)
//        doctorRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists() && isAdded) {
//                    // Get doctor data
//                    val firstName = dataSnapshot.child("firstName").getValue(String::class.java) ?: ""
//                    val lastName = dataSnapshot.child("lastName").getValue(String::class.java) ?: ""
//                    val fullName = "Dr. $firstName $lastName"
//
//                    // Set welcome text with actual name
//                    welcomeTextView.text = "Welcome, $fullName!"
//
//                    // Set other fields
//                    emailTextView.text = dataSnapshot.child("email").getValue(String::class.java) ?: ""
//                    phoneTextView.text = dataSnapshot.child("phoneNumber").getValue(String::class.java) ?: ""
//                    userTypeTextView.text = userType
//                    doctorIdTextView.text = dataSnapshot.child("medicalId").getValue(String::class.java) ?: ""
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                if (isAdded) {
//                    Toast.makeText(requireContext(), "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
//    }
//
//    private fun loadPatientData() {
//        val patientRef = mDatabase.child("users").child("patients").child(userId)
//        patientRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists() && isAdded) {
//                    // Get patient data
//                    val firstName = dataSnapshot.child("firstName").getValue(String::class.java) ?: ""
//                    val lastName = dataSnapshot.child("lastName").getValue(String::class.java) ?: ""
//                    val fullName = "$firstName $lastName"
//
//                    // Set welcome text with actual name
//                    welcomeTextView.text = "Welcome, $fullName!"
//
//                    // Set other fields
//                    emailTextView.text = dataSnapshot.child("email").getValue(String::class.java) ?: ""
//                    phoneTextView.text = dataSnapshot.child("phoneNumber").getValue(String::class.java) ?: ""
//                    genderTextView.text = dataSnapshot.child("gender").getValue(String::class.java) ?: ""
//                    dobTextView.text = dataSnapshot.child("dob").getValue(String::class.java) ?: ""
//                    bloodTextView.text = dataSnapshot.child("bloodType").getValue(String::class.java) ?: ""
//                    heightTextView.text = dataSnapshot.child("height").getValue(String::class.java) ?: ""
//                    weightTextView.text = dataSnapshot.child("weight").getValue(String::class.java) ?: ""
//                    nidTextView.text = dataSnapshot.child("nid").getValue(String::class.java) ?: ""
//                    userTypeTextView.text = userType
//                    doctorIdTextView.text = dataSnapshot.child("doctorId").getValue(String::class.java) ?: ""
//
//                    // Set health data fields
//                    diabetesTextView.text = dataSnapshot.child("diabetes").getValue(String::class.java) ?: "N/A"
//                    bloodPressureTextView.text = dataSnapshot.child("bloodPressure").getValue(String::class.java) ?: "N/A"
//                    liverDiseaseTextView.text = dataSnapshot.child("liverDisease").getValue(String::class.java) ?: "N/A"
//                    pregnancyStatusTextView.text = dataSnapshot.child("pregnancyStatus").getValue(String::class.java) ?: "N/A"
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                if (isAdded) {
//                    Toast.makeText(requireContext(), "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
//    }
//
//    private fun navigateToEditProfile() {
//        // Create an intent to start the EditProfileActivity
//        val intent = Intent(requireContext(), EditProfileActivity::class.java).apply {
//            // Pass necessary data to edit profile activity
//            putExtra("USER_ID", userId)
//            putExtra("USER_TYPE", userType)
//        }
//        startActivity(intent)
//    }
//
//    private fun logoutUser() {
//        mAuth.signOut()
//        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
//        // Redirect to login screen (replace LoginActivity with your login activity)
//        val intent = Intent(requireContext(), MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//        requireActivity().finish()
//    }
//}