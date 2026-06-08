package com.ankara.bote.randevu.ui.academician

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ankara.bote.randevu.R
import com.ankara.bote.randevu.data.model.Appointment
import com.ankara.bote.randevu.databinding.ItemAppointmentRequestBinding

class AppointmentRequestAdapter(
    private val onApprove: (Appointment) -> Unit,
    private val onReject: (Appointment) -> Unit
) : ListAdapter<Appointment, AppointmentRequestAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAppointmentRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemAppointmentRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Appointment) {
            binding.tvStudentName.text = item.studentName
            binding.tvAppointmentType.text = if (item.groupId == null) "Bireysel Randevu" else "Grup Randevusu"
            binding.tvDateTime.text = "${item.date} | ${item.startTime}"
            binding.chipStatus.text = item.status

            when (item.status) {
                "PENDING" -> {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.accent)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                    binding.actionButtons.visibility = android.view.View.VISIBLE
                }
                "APPROVED" -> {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.success)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                    binding.actionButtons.visibility = android.view.View.GONE
                }
                "REJECTED" -> {
                    binding.chipStatus.setChipBackgroundColorResource(R.color.error)
                    binding.chipStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                    binding.actionButtons.visibility = android.view.View.GONE
                }
                else -> {
                    binding.actionButtons.visibility = android.view.View.GONE
                }
            }

            binding.btnApprove.setOnClickListener { onApprove(item) }
            binding.btnReject.setOnClickListener { onReject(item) }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment) = oldItem == newItem
    }
}
