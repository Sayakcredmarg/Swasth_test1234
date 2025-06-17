package com.example.swasth

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditProfileActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var bloodTypeEditText: EditText
    private lateinit var diabetesEditText: EditText
    private lateinit var bloodPressureEditText: EditText
    private lateinit var liverDiseaseEditText: EditText
    private lateinit var pregnancyStatusEditText: EditText
    private lateinit var ageEditText: EditText

    private lateinit var diabetesTextView: TextView
    private lateinit var bloodPressureTextView: TextView
    private lateinit var liverDiseaseTextView: TextView
    private lateinit var pregnancyStatusTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var heightTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var bloodTypeTextView: TextView

    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private lateinit var mDatabase: DatabaseReference
    private lateinit var userId: String
    private lateinit var userType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Enable the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Profile"

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().reference

        // Get userId and userType from intent
        userId = intent.getStringExtra("USER_ID") ?: ""
        userType = intent.getStringExtra("USER_TYPE") ?: ""

        if (userId.isEmpty() || userType.isEmpty()) {
            Toast.makeText(this, "Error: User information not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize UI elements
        initializeViews()

        // Configure views based on user type (doctor or patient)
        configureViewsBasedOnUserType()

        // Load user data from Firebase
        loadUserData()

        // Set up button listeners
        setupButtonListeners()
    }

    private fun initializeViews() {
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        heightEditText = findViewById(R.id.heightEditText)
        weightEditText = findViewById(R.id.weightEditText)
        bloodTypeEditText = findViewById(R.id.bloodTypeEditText)
        diabetesEditText = findViewById(R.id.diabetesEditText)
        bloodPressureEditText = findViewById(R.id.bloodPressureEditText)
        liverDiseaseEditText = findViewById(R.id.liverDiseaseEditText)
        pregnancyStatusEditText = findViewById(R.id.pregnancyStatusEditText)
        ageEditText = findViewById(R.id.ageEditText)

        // TextView labels
        diabetesTextView = findViewById(R.id.diabetesTextView)
        bloodPressureTextView = findViewById(R.id.bloodPressureTextView)
        liverDiseaseTextView = findViewById(R.id.liverDiseaseTextView)
        pregnancyStatusTextView = findViewById(R.id.pregnancyStatusTextView)
        ageTextView = findViewById(R.id.ageTextView)
        heightTextView = findViewById(R.id.heightTextView)
        weightTextView = findViewById(R.id.weightTextView)
        bloodTypeTextView = findViewById(R.id.bloodTypeTextView)

        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
    }

    private fun configureViewsBasedOnUserType() {
        if (userType == "Doctor") {
            // Hide fields not applicable to doctors
            heightEditText.visibility = android.view.View.GONE
            weightEditText.visibility = android.view.View.GONE
            bloodTypeEditText.visibility = android.view.View.GONE
            diabetesEditText.visibility = android.view.View.GONE
            bloodPressureEditText.visibility = android.view.View.GONE
            liverDiseaseEditText.visibility = android.view.View.GONE
            pregnancyStatusEditText.visibility = android.view.View.GONE
            ageEditText.visibility = android.view.View.GONE

            // Hide corresponding labels
            heightTextView.visibility = android.view.View.GONE
            weightTextView.visibility = android.view.View.GONE
            bloodTypeTextView.visibility = android.view.View.GONE
            diabetesTextView.visibility = android.view.View.GONE
            bloodPressureTextView.visibility = android.view.View.GONE
            liverDiseaseTextView.visibility = android.view.View.GONE
            pregnancyStatusTextView.visibility = android.view.View.GONE
            ageTextView.visibility = android.view.View.GONE
        } else {
            // For patients, show health fields but not age field
            ageTextView.text = "Age"
        }
    }

    private fun loadUserData() {
        val userRef = if (userType == "Doctor") {
            mDatabase.child("users").child("doctors").child(userId)
        } else {
            mDatabase.child("users").child("patients").child(userId)
        }

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Populate common fields
                    firstNameEditText.setText(dataSnapshot.child("firstName").getValue(String::class.java) ?: "")
                    lastNameEditText.setText(dataSnapshot.child("lastName").getValue(String::class.java) ?: "")
                    phoneEditText.setText(dataSnapshot.child("phoneNumber").getValue(String::class.java) ?: "")

                    if (userType == "Doctor") {
                        // Doctor-specific fields
                        // No additional fields needed for now
                    } else {
                        // Patient-specific fields
                        heightEditText.setText(dataSnapshot.child("height").getValue(String::class.java) ?: "")
                        weightEditText.setText(dataSnapshot.child("weight").getValue(String::class.java) ?: "")
                        bloodTypeEditText.setText(dataSnapshot.child("bloodType").getValue(String::class.java) ?: "")
                        diabetesEditText.setText(dataSnapshot.child("diabetes").getValue(String::class.java) ?: "")
                        bloodPressureEditText.setText(dataSnapshot.child("bloodPressure").getValue(String::class.java) ?: "")
                        liverDiseaseEditText.setText(dataSnapshot.child("liverDisease").getValue(String::class.java) ?: "")
                        pregnancyStatusEditText.setText(dataSnapshot.child("pregnancyStatus").getValue(String::class.java) ?: "")
                        ageEditText.setText(dataSnapshot.child("age").getValue(String::class.java) ?: "")
                    }
                } else {
                    Toast.makeText(this@EditProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@EditProfileActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun setupButtonListeners() {
        saveButton.setOnClickListener {
            saveUserData()
        }

        cancelButton.setOnClickListener {
            // Just close the activity without saving
            finish()
        }
    }

    private fun saveUserData() {
        // Validate fields
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Create updates map
        val updates = HashMap<String, Any>()
        updates["firstName"] = firstName
        updates["lastName"] = lastName
        updates["phoneNumber"] = phone

        // Add user type specific fields
        if (userType == "Patient") {
            val height = heightEditText.text.toString().trim()
            val weight = weightEditText.text.toString().trim()
            val bloodType = bloodTypeEditText.text.toString().trim()
            val diabetes = diabetesEditText.text.toString().trim()
            val bloodPressure = bloodPressureEditText.text.toString().trim()
            val liverDisease = liverDiseaseEditText.text.toString().trim()
            val pregnancyStatus = pregnancyStatusEditText.text.toString().trim()
            val Age = ageEditText.text.toString().trim()

            updates["height"] = height
            updates["weight"] = weight
            updates["bloodType"] = bloodType
            updates["diabetes"] = diabetes
            updates["bloodPressure"] = bloodPressure
            updates["liverDisease"] = liverDisease
            updates["pregnancyStatus"] = pregnancyStatus
            updates["age"] = Age
        }

        // Update the database
        val userRef = if (userType == "Doctor") {
            mDatabase.child("users").child("doctors").child(userId)
        } else {
            mDatabase.child("users").child("patients").child(userId)
        }

        userRef.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
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