package com.example.swasth.repository

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.swasth.R
import com.example.swasth.model.Doctor
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView

class DoctorsAdapter(
    private var doctors: List<Doctor>,
    private val onBookAppointmentClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDoctorImage: CircleImageView = itemView.findViewById(R.id.ivDoctorImage)
        val tvDoctorName: TextView = itemView.findViewById(R.id.fullName)
        val tvSpecialization: TextView = itemView.findViewById(R.id.tvSpecialization)
        val tvExperience: TextView = itemView.findViewById(R.id.tvExperience)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val btnBookAppointment: MaterialButton = itemView.findViewById(R.id.btnBookAppointment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]

        holder.tvDoctorName.text = doctor.fullName
        holder.tvSpecialization.text = doctor.specialization
        holder.tvExperience.text = "${doctor.experience} years experience"
        holder.tvRating.text = doctor.rating.toString()

        Glide.with(holder.itemView.context)
            .load(doctor.imageUrl)
            .placeholder(R.drawable.ic_doctor_placeholder)
            .into(holder.ivDoctorImage)

        holder.btnBookAppointment.setOnClickListener {
            onBookAppointmentClick(doctor)
        }
    }

    override fun getItemCount() = doctors.size

    fun updateDoctors(newList: List<Doctor>) {
        doctors = newList
        notifyDataSetChanged()
    }
}
