package com.ankara.bote.randevu.ui.appointments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ankara.bote.randevu.R
import com.ankara.bote.randevu.data.model.Appointment
import com.ankara.bote.randevu.databinding.ItemAppointmentBinding

class AppointmentAdapter(
    private val onCancelClick: (Appointment) -> Unit
) : ListAdapter<Appointment, AppointmentAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAppointmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: Appointment) {
            binding.tvAcademicianName.text = item.academicianName
            binding.tvDateTime.text = "${item.date} | ${item.startTime} - ${item.endTime}"
            
            // Durum Göstergesi (Luxury Look)
            binding.tvStatus.text = when(item.status) {
                "APPROVED" -> "Onaylandı"
                "PENDING" -> "Onay Bekliyor"
                "REJECTED" -> "Reddedildi"
                "CANCELLED" -> "İptal Edildi"
                else -> item.status
            }

            val statusColor = when(item.status) {
                "APPROVED" -> R.color.success
                "PENDING" -> R.color.accent
                "REJECTED", "CANCELLED" -> R.color.error
                else -> R.color.text_secondary
            }
            binding.tvStatus.setTextColor(ContextCompat.getColor(binding.root.context, statusColor))

            // İptal butonu sadece bekleyen veya onaylı randevularda görünsün
            binding.btnCancel.visibility = if (item.status == "PENDING" || item.status == "APPROVED") View.VISIBLE else View.GONE
            binding.btnCancel.setOnClickListener { onCancelClick(item) }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment) = oldItem == newItem
    }
}
