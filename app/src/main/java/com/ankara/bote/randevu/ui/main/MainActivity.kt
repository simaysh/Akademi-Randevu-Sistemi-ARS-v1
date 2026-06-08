package com.ankara.bote.randevu.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ankara.bote.randevu.R
import com.ankara.bote.randevu.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHost.navController
        
        setupMenuForRole()
        binding.bottomNav.setupWithNavController(navController)

        vm.error.observe(this) { msg ->
            msg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.bottomNav)
                    .show()
                vm.clearError()
            }
        }
        
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.qrScannerFragment, R.id.createGroupFragment -> binding.bottomNav.visibility = View.GONE
                else -> binding.bottomNav.visibility = View.VISIBLE
            }
        }
    }

    private fun setupMenuForRole() {
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        
        if (vm.session.isAcademician) {
            binding.bottomNav.menu.clear()
            binding.bottomNav.inflateMenu(R.menu.bottom_nav_academician_menu)
            graph.setStartDestination(R.id.academicianDashboardFragment)
        } else {
            binding.bottomNav.menu.clear()
            binding.bottomNav.inflateMenu(R.menu.bottom_nav_menu)
            graph.setStartDestination(R.id.calendarFragment)
        }
        
        navController.graph = graph
    }
}
