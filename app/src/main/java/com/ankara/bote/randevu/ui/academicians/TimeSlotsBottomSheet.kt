package com.ankara.bote.randevu.ui.academicians

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ankara.bote.randevu.data.model.Academician
import com.ankara.bote.randevu.data.model.TimeSlot
import com.ankara.bote.randevu.databinding.BottomSheetTimeSlotsBinding
import com.ankara.bote.randevu.ui.main.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.format.DateTimeFormatter
import java.util.Locale

class TimeSlotsBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_ID = "academicianId"
        private const val ARG_NAME = "academicianName"
        private const val ARG_TITLE = "academicianTitle"

        fun newInstance(a: Academician) = TimeSlotsBottomSheet().apply {
            arguments = Bundle().apply {
                putInt(ARG_ID, a.id)
                putString(ARG_NAME, a.name)
                putString(ARG_TITLE, a.title)
            }
        }
    }

    private var _binding: BottomSheetTimeSlotsBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by activityViewModels()
    private val displayFmt = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("tr", "TR"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetTimeSlotsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val academicianName = arguments?.getString(ARG_NAME) ?: ""
        val academicianTitle = arguments?.getString(ARG_TITLE) ?: ""
        
        binding.tvAcademicianName.text = "$academicianTitle $academicianName"
        binding.tvSelectedDateLabel.text = vm.selectedDate.value?.format(displayFmt) ?: ""

        val slotAdapter = TimeSlotAdapter { slot ->
            confirmBooking(slot, academicianName, academicianTitle)
        }
        
        binding.rvSlots.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSlots.adapter = slotAdapter

        vm.timeSlots.observe(viewLifecycleOwner) { slots ->
            slotAdapter.submitList(slots)
            binding.tvNoSlots.visibility = if (slots.isEmpty()) View.VISIBLE else View.GONE
            binding.rvSlots.visibility = if (slots.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun confirmBooking(slot: TimeSlot, name: String, title: String) {
        val groups = vm.myGroups.value ?: emptyList()
        val options = arrayOf("👤 Bireysel Randevu") + groups.map { "👥 ${it.name}" }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Randevu Türünü Seçin")
            .setItems(options) { _, which ->
                val groupId = if (which == 0) null else groups[which - 1].id
                val academician = vm.academicians.value?.find { it.id == slot.academicianId }
                
                if (academician != null) {
                    vm.bookAppointment(slot, academician, groupId)
                    Toast.makeText(requireContext(), "Randevu talebiniz iletildi. Onay bekliyor.", Toast.LENGTH_LONG).show()
                    dismiss()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
