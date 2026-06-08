package com.ankara.bote.randevu.ui.academicians

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ankara.bote.randevu.data.model.Academician
import com.ankara.bote.randevu.databinding.FragmentAcademiciansBinding
import com.ankara.bote.randevu.ui.main.MainViewModel

class AcademiciansFragment : Fragment() {

    private var _binding: FragmentAcademiciansBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAcademiciansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AcademicianAdapter { academician ->
            vm.selectAcademician(academician.id)
        }
        
        binding.rvAcademicians.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAcademicians.adapter = adapter

        vm.academicians.observe(viewLifecycleOwner) { 
            adapter.submitList(it) 
        }

        // QR tarandığında veya listeden seçildiğinde BottomSheet'i otomatik aç
        vm.openTimeSlotsFor.observe(viewLifecycleOwner) { academician ->
            if (academician != null) {
                openTimeSlotsSheet(academician)
                vm.onTimeSlotsOpened() // Tetikleyiciyi sıfırla
            }
        }

        vm.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    private fun openTimeSlotsSheet(academician: Academician) {
        val sheet = TimeSlotsBottomSheet.newInstance(academician)
        sheet.show(childFragmentManager, "slots")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
