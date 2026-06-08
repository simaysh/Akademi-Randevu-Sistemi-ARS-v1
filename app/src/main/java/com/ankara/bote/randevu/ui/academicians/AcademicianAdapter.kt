package com.ankara.bote.randevu.ui.academicians

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ankara.bote.randevu.data.model.Academician
import com.ankara.bote.randevu.databinding.ItemAcademicianBinding

class AcademicianAdapter(
    private val onViewSlots: (Academician) -> Unit
) : ListAdapter<Academician, AcademicianAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemAcademicianBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(a: Academician) {
            b.tvName.text = a.name
            b.tvTitle.text = a.title
            b.tvEmail.text = a.email
            b.btnViewSlots.setOnClickListener { onViewSlots(a) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAcademicianBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Academician>() {
            override fun areItemsTheSame(a: Academician, b: Academician) = a.id == b.id
            override fun areContentsTheSame(a: Academician, b: Academician) = a == b
        }
    }
}
