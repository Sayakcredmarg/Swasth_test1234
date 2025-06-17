package com.example.swasth.repository

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swasth.R
import com.example.swasth.model.Medication

class MedicationAdapter(private val medications: List<Medication>) :
    RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

    inner class MedicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvUseCase: TextView = view.findViewById(R.id.tvUseCase)
        val tvDosage: TextView = view.findViewById(R.id.tvDosage)
        val tvDoctorNote: TextView = view.findViewById(R.id.tvDoctorNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medication, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val med = medications[position]
        holder.tvName.text = med.name
        holder.tvUseCase.text = "Use: ${med.useCase}"
        holder.tvDosage.text = "Dosage: ${med.dosage}"
        holder.tvDoctorNote.text = "Note: ${med.doctorNote}"
    }

    override fun getItemCount(): Int = medications.size
}
