package com.example.swasth.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swasth.R
import com.example.swasth.model.Doctor
import com.example.swasth.repository.DoctorsAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase

class BookAppointmentFragment : Fragment(R.layout.fragment_book_history) {

    private lateinit var adapter: DoctorsAdapter
    private var allDoctors: List<Doctor> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.rvDoctors)
        val searchBox = view.findViewById<TextInputEditText>(R.id.etSearch)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val emptyState = view.findViewById<LinearLayout>(R.id.emptyState)

        searchBox.isEnabled = false
        adapter = DoctorsAdapter(emptyList()) { doctor -> showBookingDialog(doctor) }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        fetchDoctors(progressBar, emptyState, searchBox)

        searchBox.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                val filtered = allDoctors.filter {
                    it.fullName.lowercase().contains(query) || it.specialization.lowercase().contains(query)
                }
                adapter.updateDoctors(filtered)
                recycler.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
                emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun fetchDoctors(
        progressBar: ProgressBar,
        emptyState: LinearLayout,
        searchBox: TextInputEditText
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users/doctors")
        progressBar.visibility = View.VISIBLE

        dbRef.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<Doctor>()
            for (doctorSnap in snapshot.children) {
                val doctor = doctorSnap.getValue(Doctor::class.java)
                doctor?.let {
                    list.add(it.copy(id = doctorSnap.key ?: ""))
                }
            }

            progressBar.visibility = View.GONE
            allDoctors = list
            if (list.isEmpty()) {
                emptyState.visibility = View.VISIBLE
            } else {
                searchBox.isEnabled = true
                adapter.updateDoctors(list)
            }

        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "Error loading doctors", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showBookingDialog(doctor: Doctor) {
        val input = EditText(requireContext()).apply {
            hint = "Enter your name"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Book with ${doctor.fullName}")
            .setView(input)
            .setPositiveButton("Confirm") { _, _ ->
                val patientName = input.text.toString().trim()
                if (patientName.isNotEmpty()) {
                    bookAppointment(doctor, patientName)
                } else {
                    Toast.makeText(requireContext(), "Name required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun bookAppointment(doctor: Doctor, patientName: String) {
        val appointmentRef = FirebaseDatabase.getInstance()
            .getReference("appointments")
            .child(doctor.id) // appointment under each doctor

        val appointmentId = appointmentRef.push().key ?: return

        val appointmentData = mapOf(
            "appointmentId" to appointmentId,
            "doctorId" to doctor.id,
            "doctorName" to doctor.fullName,
            "patientName" to patientName,
            "status" to "pending",
            "timestamp" to System.currentTimeMillis()
        )

        appointmentRef.child(appointmentId).setValue(appointmentData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Appointment confirmed!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Booking failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

}
