package com.ankara.bote.randevu.ui.academicians

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ankara.bote.randevu.data.model.TimeSlot
import com.ankara.bote.randevu.databinding.ItemTimeSlotBinding

class TimeSlotAdapter(
    private val onBook: (TimeSlot) -> Unit
) : ListAdapter<TimeSlot, TimeSlotAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemTimeSlotBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(slot: TimeSlot) {
            b.tvTime.text = "${slot.startTime} – ${slot.endTime}"
            b.tvGroupSize.text = "Maks. ${slot.maxGroupSize} kişi"
            b.btnBook.setOnClickListener { onBook(slot) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, pos: Int) = holder.bind(getItem(pos))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<TimeSlot>() {
            override fun areItemsTheSame(a: TimeSlot, b: TimeSlot) = a.id == b.id
            override fun areContentsTheSame(a: TimeSlot, b: TimeSlot) = a == b
        }
    }
}
