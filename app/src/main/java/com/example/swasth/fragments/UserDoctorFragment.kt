package com.example.swasth.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.swasth.EditdoctorProfile
import com.example.swasth.MainActivity
import com.example.swasth.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DoctorUserFragment : Fragment() {

    // Doctor profile image and name
    private lateinit var doctorImageView: ImageView
    private lateinit var doctorNameTextView: TextView
    private lateinit var specializationHeaderTextView: TextView

    // Basic information fields
    private lateinit var welcomeTextView: TextView
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var specializationTextView: TextView
    private lateinit var experienceTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var userTypeTextView: TextView

    // Professional details fields
    private lateinit var addressTextView: TextView
    private lateinit var consultationFeeTextView: TextView
    private lateinit var ratingTextView: TextView

    // Buttons
    private lateinit var editDocProfileButton: Button
    private lateinit var logoutButton: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var userId: String
    private val userType = "Doctor"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment (doctor layout)
        return inflater.inflate(R.layout.fragment_user_doctor, container, false)
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
        editDocProfileButton.setOnClickListener {
            navigateToEditProfile()
        }

        // Get current user and load data
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            email?.let { findDoctorByEmail(it) }
        } else {
            // Not logged in, show message
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initializeViews(view: View) {
        // Doctor profile section
        doctorImageView = view.findViewById(R.id.doctorImageView)
        doctorNameTextView = view.findViewById(R.id.doctorNameTextView)
        specializationHeaderTextView = view.findViewById(R.id.specializationHeaderTextView)

        // Basic information
        welcomeTextView = view.findViewById(R.id.welcomeTextView)
        firstNameTextView = view.findViewById(R.id.firstNameTextView)
        lastNameTextView = view.findViewById(R.id.lastNameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        specializationTextView = view.findViewById(R.id.specializationTextView)
        experienceTextView = view.findViewById(R.id.experienceTextView)
        phoneTextView = view.findViewById(R.id.phoneTextView)
        userTypeTextView = view.findViewById(R.id.userTypeTextView)

        // Professional details
        addressTextView = view.findViewById(R.id.addressTextView)
        consultationFeeTextView = view.findViewById(R.id.consultationFeeTextView)
        ratingTextView = view.findViewById(R.id.ratingTextView)

        // Buttons
        editDocProfileButton = view.findViewById(R.id.editDocProfileButton)
    }

    private fun findDoctorByEmail(email: String) {
        val doctorsRef = mDatabase.child("users").child("doctors")
        doctorsRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User is a doctor
                    for (doctorSnapshot in dataSnapshot.children) {
                        userId = doctorSnapshot.key ?: ""
                        loadDoctorData()
                        return
                    }
                } else {
                    // Doctor not found
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Doctor profile not found", Toast.LENGTH_SHORT).show()
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

    private fun loadDoctorData() {
        val doctorRef = mDatabase.child("users").child("doctors").child(userId)
        doctorRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && isAdded) {
                    try {
                        // Get basic string data
                        val firstName = dataSnapshot.child("firstName").getValue(String::class.java) ?: ""
                        val lastName = dataSnapshot.child("lastName").getValue(String::class.java) ?: ""
                        val fullName = "Dr. $firstName $lastName"
                        val specialization = dataSnapshot.child("specialization").getValue(String::class.java) ?: ""
                        val email = dataSnapshot.child("email").getValue(String::class.java) ?: ""
                        val phone = dataSnapshot.child("phone").getValue(String::class.java) ?: ""
                        val address = dataSnapshot.child("address").getValue(String::class.java) ?: ""
                        val imageUrl = dataSnapshot.child("imageUrl").getValue(String::class.java) ?: ""
                        val defaultImageUrl = "https://randomuser.me/api/portraits/men/78.jpg"
                        val finalImageUrl = if (imageUrl.isNotEmpty()) imageUrl else defaultImageUrl
                        // Handle numeric fields that might be stored as Long or String
                        val experience = when (val expValue = dataSnapshot.child("experience").value) {
                            is Long -> expValue.toString()
                            is String -> expValue
                            else -> "0"
                        }

                        val consultationFee = when (val feeValue = dataSnapshot.child("consultationFee").value) {
                            is Long -> feeValue.toString()
                            is Double -> feeValue.toString()
                            is String -> feeValue
                            else -> "0"
                        }

                        val rating = when (val ratingValue = dataSnapshot.child("rating").value) {
                            is Long -> ratingValue.toString()
                            is Double -> String.format("%.1f", ratingValue)
                            is String -> ratingValue
                            else -> "0"
                        }

                        // Set profile section
                        doctorNameTextView.text = fullName
                        specializationHeaderTextView.text = specialization

                        // Load profile image
                        if (imageUrl.isNotEmpty()) {
                            Glide.with(this@DoctorUserFragment)
                                .load(finalImageUrl)
                                .placeholder(R.drawable.doctor1)
                                .error(R.drawable.doctor1)
                                .circleCrop()
                                .into(doctorImageView)
                        } else {
                            Glide.with(this@DoctorUserFragment)
                                .load(finalImageUrl)
                                .placeholder(R.drawable.doctor1)
                                .error(R.drawable.doctor1)
                                .circleCrop()
                                .into(doctorImageView)
                        }

                        // Set welcome text
                        welcomeTextView.text = "Welcome, $fullName!"

                        // Set basic information fields
                        firstNameTextView.text = "First Name: $firstName"
                        lastNameTextView.text = "Last Name: $lastName"
                        emailTextView.text = "Email: $email"
                        specializationTextView.text = "Specialization: $specialization"
                        experienceTextView.text = "Experience: $experience years"
                        phoneTextView.text = "Phone: $phone"
                        userTypeTextView.text = "User Type: $userType"

                        // Set professional details
                        addressTextView.text = "Address: $address"
                        consultationFeeTextView.text = "Consultation Fee: $$consultationFee"
                        ratingTextView.text = "Rating: $rating/5"

                    } catch (e: Exception) {
                        Log.e("DoctorFragment", "Error parsing data: ${e.message}")
                        if (isAdded) {
                            Toast.makeText(requireContext(), "Error loading profile data", Toast.LENGTH_SHORT).show()
                        }
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

    private fun navigateToEditProfile() {
        // Create an intent to start the EditProfileActivity
        val intent = Intent(requireContext(), EditdoctorProfile::class.java).apply {
            // Pass necessary data to edit profile activity
            putExtra("DOCTOR_ID", userId)
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