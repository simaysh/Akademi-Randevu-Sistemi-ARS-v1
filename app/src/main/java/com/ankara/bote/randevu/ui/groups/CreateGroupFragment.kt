package com.ankara.bote.randevu.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ankara.bote.randevu.databinding.FragmentCreateGroupBinding
import com.ankara.bote.randevu.ui.main.MainViewModel
import kotlinx.coroutines.launch

class CreateGroupFragment : Fragment() {

    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSaveGroup.setOnClickListener {
            val name = binding.etGroupName.text.toString().trim()
            val numbersStr = binding.etMemberNumbers.text.toString()

            if (name.isEmpty()) {
                binding.etGroupName.error = "Lutfen gruba bir isim verin"
                return@setOnClickListener
            }

            val numbers = numbersStr.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            lifecycleScope.launch {
                val memberIds = numbers.map { num ->
                    vm.repository.ensureStudentExists(num)
                }
                vm.createGroupSync(name, memberIds)
                Toast.makeText(
                    requireContext(),
                    "Grup basariyla kuruldu ve uyeler eklendi",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}