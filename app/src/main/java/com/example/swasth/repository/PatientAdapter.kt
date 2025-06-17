package com.example.swasth.repository

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swasth.R
import com.example.swasth.model.Patient

class PatientAdapter : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    private var patients: List<Patient> = emptyList()

    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvPatientName)
        val tvDetails: TextView = itemView.findViewById(R.id.tvPatientDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.patient_card_item, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        holder.tvName.text = patient.name
        holder.tvDetails.text = " Last Visit: ${patient.lastVisitDate}"
    }

    override fun getItemCount(): Int = patients.size

    fun updateData(newPatients: List<Patient>) {
        patients = newPatients
        notifyDataSetChanged()
    }
}
