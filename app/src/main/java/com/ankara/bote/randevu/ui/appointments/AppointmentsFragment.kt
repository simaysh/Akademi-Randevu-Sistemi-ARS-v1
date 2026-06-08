package com.ankara.bote.randevu.ui.appointments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ankara.bote.randevu.databinding.FragmentAppointmentsBinding
import com.ankara.bote.randevu.ui.main.MainViewModel

class AppointmentsFragment : Fragment() {

    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentAppointmentsBinding.inflate(inflater, container, false)
        .also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AppointmentAdapter { appointment ->
            vm.cancelAppointment(appointment)
        }

        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAppointments.adapter = adapter

        vm.myAppointments.observe(viewLifecycleOwner) { appointments ->
            adapter.submitList(appointments)
            binding.tvEmpty.visibility = if (appointments.isEmpty()) View.VISIBLE else View.GONE
            binding.rvAppointments.visibility = if (appointments.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
