package com.example.swasth

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditdoctorProfile : AppCompatActivity() {

    // UI Elements
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var specializationEditText: EditText
    private lateinit var experienceEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var consultationFeeEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    // Firebase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var doctorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editdoctor_profile)

        // Enable the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Doctor Profile"

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().reference

        // Get doctorId from intent
        doctorId = intent.getStringExtra("DOCTOR_ID") ?: ""

        if (doctorId.isEmpty()) {
            Toast.makeText(this, "Error: Doctor ID not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializeViews()
        loadDoctorData()
        setupClickListeners()
    }

    private fun initializeViews() {
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        specializationEditText = findViewById(R.id.specializationEditText)
        experienceEditText = findViewById(R.id.experienceEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        addressEditText = findViewById(R.id.addressEditText)
        consultationFeeEditText = findViewById(R.id.consultationFeeEditText)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
    }

    private fun loadDoctorData() {
        val doctorRef = mDatabase.child("users").child("doctors").child(doctorId)

        doctorRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Populate fields with existing doctor data
                    firstNameEditText.setText(dataSnapshot.child("firstName").getValue(String::class.java) ?: "")
                    lastNameEditText.setText(dataSnapshot.child("lastName").getValue(String::class.java) ?: "")
                    emailEditText.setText(dataSnapshot.child("email").getValue(String::class.java) ?: "")
                    specializationEditText.setText(dataSnapshot.child("specialization").getValue(String::class.java) ?: "")
                    experienceEditText.setText(dataSnapshot.child("experience").getValue(String::class.java) ?: "")
                    phoneEditText.setText(dataSnapshot.child("phoneNumber").getValue(String::class.java) ?: "")
                    addressEditText.setText(dataSnapshot.child("address").getValue(String::class.java) ?: "")
                    consultationFeeEditText.setText(dataSnapshot.child("consultationFee").getValue(String::class.java) ?: "")
                } else {
                    Toast.makeText(this@EditdoctorProfile, "Doctor data not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@EditdoctorProfile, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun setupClickListeners() {
        saveButton.setOnClickListener {
            if (validateInput()) {
                saveDoctorProfile()
            }
        }

        cancelButton.setOnClickListener {
            finish() // Close the activity without saving
        }
    }

    private fun validateInput(): Boolean {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val specialization = specializationEditText.text.toString().trim()
        val experience = experienceEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val consultationFee = consultationFeeEditText.text.toString().trim()

        // Validate required fields
        if (firstName.isEmpty()) {
            firstNameEditText.error = "First name is required"
            firstNameEditText.requestFocus()
            return false
        }

        if (lastName.isEmpty()) {
            lastNameEditText.error = "Last name is required"
            lastNameEditText.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            emailEditText.requestFocus()
            return false
        }

        if (!isValidEmail(email)) {
            emailEditText.error = "Please enter a valid email"
            emailEditText.requestFocus()
            return false
        }

        if (specialization.isEmpty()) {
            specializationEditText.error = "Specialization is required"
            specializationEditText.requestFocus()
            return false
        }

        if (experience.isEmpty()) {
            experienceEditText.error = "Experience is required"
            experienceEditText.requestFocus()
            return false
        }

        if (phone.isEmpty()) {
            phoneEditText.error = "Phone number is required"
            phoneEditText.requestFocus()
            return false
        }

        if (address.isEmpty()) {
            addressEditText.error = "Address is required"
            addressEditText.requestFocus()
            return false
        }

        if (consultationFee.isEmpty()) {
            consultationFeeEditText.error = "Consultation fee is required"
            consultationFeeEditText.requestFocus()
            return false
        }

        // Validate numeric fields
        try {
            val experienceInt = experience.toInt()
            if (experienceInt < 0) {
                experienceEditText.error = "Experience must be a positive number"
                experienceEditText.requestFocus()
                return false
            }
        } catch (e: NumberFormatException) {
            experienceEditText.error = "Please enter a valid number"
            experienceEditText.requestFocus()
            return false
        }

        try {
            val feeDouble = consultationFee.toDouble()
            if (feeDouble < 0) {
                consultationFeeEditText.error = "Consultation fee must be a positive amount"
                consultationFeeEditText.requestFocus()
                return false
            }
        } catch (e: NumberFormatException) {
            consultationFeeEditText.error = "Please enter a valid amount"
            consultationFeeEditText.requestFocus()
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun saveDoctorProfile() {
        // Get field values
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val specialization = specializationEditText.text.toString().trim()
        val experience = experienceEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val consultationFee = consultationFeeEditText.text.toString().trim()

        // Create updates map
        val updates = HashMap<String, Any>()
        updates["firstName"] = firstName
        updates["lastName"] = lastName
        updates["email"] = email
        updates["specialization"] = specialization
        updates["experience"] = experience
        updates["phoneNumber"] = phone
        updates["address"] = address
        updates["consultationFee"] = consultationFee

        // Update the database
        val doctorRef = mDatabase.child("users").child("doctors").child(doctorId)

        doctorRef.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Doctor profile updated successfully", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}