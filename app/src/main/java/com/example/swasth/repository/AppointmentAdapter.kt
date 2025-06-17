package com.example.swasth.repository

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.swasth.R
import com.example.swasth.model.Appointment
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppointmentAdapter(
    private val appointments: List<Appointment>
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val patientName: TextView = view.findViewById(R.id.tvPatientName)
//        val patientAge = view.findViewById<TextView>(R.id.tvPatientAge)
        val status: TextView = view.findViewById(R.id.tvStatus)
        val btnAccept: Button = view.findViewById(R.id.btnAccept)
        val btnReject: Button = view.findViewById(R.id.btnReject)
        val time: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appt = appointments[position]
        val context = holder.itemView.context

        holder.patientName.text = "Patient: ${appt.patientName}"
//        holder.patientAge.text = "Age: ${appt.age}"
        holder.status.text = "Status: ${appt.status}"

        val date = Date(appt.timestamp)
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        holder.time.text = "Time: ${formatter.format(date)}"

        if (appt.status != "pending") {
            holder.btnAccept.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
        }

        holder.btnAccept.setOnClickListener {
            updateStatus(context, appt.doctorId, appt.appointmentId, "accepted")
        }

        holder.btnReject.setOnClickListener {
            updateStatus(context, appt.doctorId, appt.appointmentId, "rejected")
        }
    }

    private fun updateStatus(context: Context, doctorId: String, appointmentId: String, newStatus: String) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("appointments")
            .child(doctorId)
            .child(appointmentId)
            .child("status")

        ref.setValue(newStatus)
            .addOnSuccessListener {
                Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int = appointments.size
}
