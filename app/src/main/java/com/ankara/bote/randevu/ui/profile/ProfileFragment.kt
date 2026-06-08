package com.ankara.bote.randevu.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ankara.bote.randevu.R
import com.ankara.bote.randevu.databinding.FragmentProfileBinding
import com.ankara.bote.randevu.ui.auth.LoginActivity
import com.ankara.bote.randevu.ui.main.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvUserName.text = vm.session.userName
        binding.tvUserDetail.text = vm.session.userNumber
        binding.chipRole.text = vm.session.userRole

        // Akademisyen ise bazı butonları gizle veya değiştir
        if (vm.session.isAcademician) {
            binding.btnQrScanner.visibility = View.GONE
            binding.btnPastAppointments.text = "Geçmiş Talepler"
        }

        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Oturumu Kapat")
                .setMessage("Oturumu kapatmak istediğinize emin misiniz?")
                .setPositiveButton("Evet") { _, _ ->
                    vm.session.clear()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("Hayır", null)
                .show()
        }

        binding.btnPastAppointments.setOnClickListener {
            findNavController().navigate(R.id.pastAppointmentsFragment)
        }

        binding.btnQrScanner.setOnClickListener {
            findNavController().navigate(R.id.qrScannerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
