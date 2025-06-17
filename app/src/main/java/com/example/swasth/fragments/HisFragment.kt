package com.example.swasth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swasth.R
import com.example.swasth.model.Appointment
import com.example.swasth.repository.AppointmentAdapter
//
class HisFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter

    private val appointmentList = mutableListOf<Appointment>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_his, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAppointments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AppointmentAdapter(appointmentList)
        recyclerView.adapter = adapter

        loadAppointments()

        return view
    }

    private fun loadAppointments() {
        // TODO: Replace this with real data fetching (e.g. from API or database)
        val dummyData = listOf(
            Appointment("John Doe", "Flu and Fever", "2025-05-20"),
            Appointment("Jane Smith", "Routine Checkup", "2025-05-22"),
            Appointment("Alex Johnson", "Back Pain", "2025-05-24")
        )

        appointmentList.clear()
        appointmentList.addAll(dummyData)
        adapter.notifyDataSetChanged()
    }
}
