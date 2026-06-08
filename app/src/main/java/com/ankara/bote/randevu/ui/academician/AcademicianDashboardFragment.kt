package com.ankara.bote.randevu.ui.academician

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ankara.bote.randevu.databinding.FragmentAcademicianDashboardBinding
import com.ankara.bote.randevu.ui.main.MainViewModel

class AcademicianDashboardFragment : Fragment() {

    private var _binding: FragmentAcademicianDashboardBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAcademicianDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AppointmentRequestAdapter(
            onApprove = { appt ->
                vm.updateAppointmentStatus(appt.id, "APPROVED")
                Toast.makeText(requireContext(), "${appt.studentName} randevusu onaylandı", Toast.LENGTH_SHORT).show()
            },
            onReject = { appt ->
                vm.updateAppointmentStatus(appt.id, "REJECTED")
                Toast.makeText(requireContext(), "Randevu reddedildi", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvRequests.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRequests.adapter = adapter

        vm.academicianRequests.observe(viewLifecycleOwner) { requests ->
            // Sadece bekleyen talepleri göster
            val pending = requests.filter { it.status == "PENDING" }
            adapter.submitList(pending)
            binding.tvEmptyRequests.visibility = if (pending.isEmpty()) View.VISIBLE else View.GONE
            binding.rvRequests.visibility = if (pending.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
