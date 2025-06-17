package com.example.swasth


import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.swasth.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.loginBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = firebaseAuth.currentUser?.uid

                            if (uid != null) {
                                // Check if user is a patient
                                val patientRef = database.getReference("users/patients/$uid")
                                patientRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            // User is a patient
                                            startActivity(Intent(this@MainActivity, PatientDetailActivity::class.java))
                                            finish()
                                        } else {
                                            // Check if user is a doctor
                                            val doctorRef = database.getReference("users/doctors/$uid")
                                            doctorRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if (snapshot.exists()) {
                                                        // User is a doctor
                                                        startActivity(Intent(this@MainActivity, DoctordetailActivity::class.java))
                                                        finish()
                                                    } else {
                                                        Toast.makeText(this@MainActivity, "User role not found", Toast.LENGTH_SHORT).show()
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    Toast.makeText(this@MainActivity, "Error checking doctor role", Toast.LENGTH_SHORT).show()
                                                }
                                            })
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(this@MainActivity, "Error checking patient role", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }

                        } else {
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
            }
        }

        val newUserButton = findViewById<TextView>(R.id.signUpTextView)

        newUserButton.setOnClickListener {
            val intent = Intent(this, NewuserActivity::class.java)
            startActivity(intent)
        }
    }
}
























//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.swasth.databinding.ActivityMainBinding
//import com.google.firebase.auth.FirebaseAuth
//
//class MainActivity : AppCompatActivity() {
//
//    private  lateinit var binding: ActivityMainBinding
//    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var toolbar: Toolbar
//    @SuppressLint("WrongViewCast")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        enableEdgeToEdge()
//        setContentView(binding.root)
//        firebaseAuth =FirebaseAuth.getInstance()
//        binding.signUpTextView.setOnClickListener {
//            val intent = Intent(this, NewuserActivity::class.java)
//            startActivity(intent)
//        }
//        binding.loginBtn.setOnClickListener{
//            val email = binding.email.text.toString()
//            val pass = binding.passwordEditText.text.toString()
//
//            if(email.isNotEmpty() && pass.isNotEmpty()){
//                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener{
//                        if(it.isSuccessful){
//                            val intent = Intent(this, PatientDetailActivity::class.java)
//                            startActivity(intent)
//                        }
//
//                        else{
//                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            else{
//                Toast.makeText(this, "Empty Fields are not Allowed!!", Toast.LENGTH_SHORT).show()
//            }
//
//        }
//
//
//
////        setContentView(R.layout.activity_main)
//        toolbar = findViewById(R.id.toolbar)
//
////        Step 1
//        setSupportActionBar(findViewById(R.id.toolbar))
////        Step 2
//        supportActionBar?.title = "Swasth"
//        this.toolbar.setSubtitle("Swasth")
////        Step 3
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//
//        val newUserButton = findViewById<TextView>(R.id.signUpTextView)
//
//        newUserButton.setOnClickListener {
//            val intent = Intent(this, NewuserActivity::class.java)
//            startActivity(intent)
//        }
//
//
//    }
//}