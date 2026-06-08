package com.ankara.bote.randevu.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ankara.bote.randevu.R
import com.ankara.bote.randevu.databinding.FragmentGroupsBinding
import com.ankara.bote.randevu.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class GroupsFragment : Fragment() {

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GroupAdapter { group ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Grubu Sil")
                .setMessage("\"${group.name}\" grubunu silmek istiyor musunuz?")
                .setPositiveButton("Sil") { _, _ ->
                    lifecycleScope.launch {
                        vm.repository.deleteGroup(group.id)
                    }
                }
                .setNegativeButton("İptal", null)
                .show()
        }

        binding.rvGroups.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGroups.adapter = adapter

        vm.myGroups.observe(viewLifecycleOwner) { groups ->
            adapter.submitList(groups)
            binding.tvEmpty.visibility = if (groups.isEmpty()) View.VISIBLE else View.GONE
            binding.rvGroups.visibility = if (groups.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.fabCreateGroup.setOnClickListener {
            // Tam sayfa şık grup oluşturma ekranına yönlendir
            findNavController().navigate(R.id.createGroupFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
