package com.example.swasth.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swasth.R
import com.example.swasth.model.Appointment
import com.example.swasth.repository.AppointmentAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DoctorAppointmentsFragment : Fragment(R.layout.fragment_doctor_appointments) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
    private lateinit var appointmentList: MutableList<Appointment>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvAppointments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        appointmentList = mutableListOf()
        adapter = AppointmentAdapter(appointmentList)
        recyclerView.adapter = adapter

        loadAppointmentsForDoctor()
    }

    private fun loadAppointmentsForDoctor() {
        val doctorId = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("appointments").child(doctorId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                appointmentList.clear()
                for (child in snapshot.children) {
                    val appt = child.getValue(Appointment::class.java)
                    appt?.let { appointmentList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to load appointments", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
