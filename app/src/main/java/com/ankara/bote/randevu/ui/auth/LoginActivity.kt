package com.ankara.bote.randevu.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ankara.bote.randevu.databinding.ActivityLoginBinding
import com.ankara.bote.randevu.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val vm: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (vm.isAlreadyLoggedIn) {
            goToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Form geçişlerini yönet (Öğrenci/Akademisyen)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.studentForm.visibility = View.VISIBLE
                    binding.academicianForm.visibility = View.GONE
                } else {
                    binding.studentForm.visibility = View.GONE
                    binding.academicianForm.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.btnLogin.setOnClickListener {
            if (binding.tabLayout.selectedTabPosition == 0) {
                // Öğrenci Girişi
                vm.loginStudent(
                    binding.etStudentNumber.text.toString(),
                    binding.etFirstName.text.toString(),
                    binding.etLastName.text.toString()
                )
            } else {
                // Akademisyen Girişi
                vm.loginAcademician(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                )
            }
        }

        vm.state.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is AuthState.Success -> goToMain()
                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.btnLogin)
                        .show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
