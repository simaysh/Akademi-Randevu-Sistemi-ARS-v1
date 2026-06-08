package com.ankara.bote.randevu.ui.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ankara.bote.randevu.data.model.Group
import com.ankara.bote.randevu.databinding.ItemGroupBinding

class GroupAdapter(
    private val onDelete: (Group) -> Unit
) : ListAdapter<Group, GroupAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemGroupBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(g: Group) {
            b.tvGroupName.text = g.name
            b.tvMemberCount.text = "Grup ID: ${g.id}"
            b.btnDeleteGroup.setOnClickListener { onDelete(g) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Group>() {
            override fun areItemsTheSame(a: Group, b: Group) = a.id == b.id
            override fun areContentsTheSame(a: Group, b: Group) = a == b
        }
    }
}
