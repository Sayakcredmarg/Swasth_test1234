package com.example.swasth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NewuserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_newuser)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val patientButton = findViewById<TextView>(R.id.patient)

        patientButton.setOnClickListener {
            val intent = Intent(this, PatientActivity::class.java)
            startActivity(intent)
        }

        val doctorButton = findViewById<TextView>(R.id.doctor)

        doctorButton.setOnClickListener {
            val intent = Intent(this, DoctorActivity::class.java)
            startActivity(intent)
        }

//        val oxygenButton = findViewById<TextView>(R.id.oxygen)

//        oxygenButton.setOnClickListener {
//            val intent = Intent(this, O2Activity::class.java)
//            startActivity(intent)
//        }

//        val medicineButton = findViewById<TextView>(R.id.medicine)

//        medicineButton.setOnClickListener {
//            val intent = Intent(this, MedicineActivity::class.java)
//            startActivity(intent)
//        }
//
//        val ambulenceButton = findViewById<TextView>(R.id.ambulence)
//
//        ambulenceButton.setOnClickListener {
//            val intent = Intent(this, AmbulenceActivity::class.java)
//            startActivity(intent)
//        }
    }
}