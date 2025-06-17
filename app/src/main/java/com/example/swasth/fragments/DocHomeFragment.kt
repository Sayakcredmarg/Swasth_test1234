package com.example.swasth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swasth.R
import com.example.swasth.VideoCallActivity
import com.example.swasth.model.Patient
import com.example.swasth.repository.PatientAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocHomeFragment : Fragment() {

    private lateinit var videoCallButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PatientAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_doc_home, container, false)

        // Initialize views
        videoCallButton = view.findViewById(R.id.videoCallButton)
        recyclerView = view.findViewById(R.id.recyclerViewPatientHistory)

        // Setup adapter
        adapter = PatientAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Load patients from Firebase
        loadAcceptedPatients()

        // Video call button listener
        videoCallButton.setOnClickListener {
            val intent = Intent(requireContext(), VideoCallActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun loadAcceptedPatients() {
        val doctorId = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("appointments").child(doctorId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val acceptedPatients = mutableListOf<Patient>()

                for (child in snapshot.children) {
                    val status = child.child("status").getValue(String::class.java)
                    if (status == "accepted") {
                        val name = child.child("patientName").getValue(String::class.java) ?: continue
                        val timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L

                        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date(timestamp))

                        val patient = Patient(name = name,  lastVisitDate = formattedDate)
                        acceptedPatients.add(patient)
                    }
                }

                adapter.updateData(acceptedPatients)
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to load accepted patients", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
