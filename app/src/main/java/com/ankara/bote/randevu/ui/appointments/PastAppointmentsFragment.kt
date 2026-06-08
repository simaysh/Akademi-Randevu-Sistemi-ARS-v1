package com.ankara.bote.randevu.ui.appointments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ankara.bote.randevu.databinding.FragmentPastAppointmentsBinding
import com.ankara.bote.randevu.ui.main.MainViewModel

class PastAppointmentsFragment : Fragment() {

    private var _binding: FragmentPastAppointmentsBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPastAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Reuse AppointmentAdapter but without cancel button for past ones
        val adapter = AppointmentAdapter { /* No action for past items */ }
        
        binding.rvPastAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPastAppointments.adapter = adapter

        vm.myPastAppointments.observe(viewLifecycleOwner) { appointments ->
            adapter.submitList(appointments)
            binding.tvEmpty.visibility = if (appointments.isEmpty()) View.VISIBLE else View.GONE
            binding.rvPastAppointments.visibility = if (appointments.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
