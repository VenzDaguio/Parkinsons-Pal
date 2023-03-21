package com.example.parkinsonspal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.parkinsonspal.Fragments.CalendarFragment
import com.example.parkinsonspal.Fragments.LogFragment
import com.example.parkinsonspal.Fragments.MedicationFragment
import com.example.parkinsonspal.Fragments.TrackFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class TrackHealth : AppCompatActivity() {

    private lateinit var viewModel: TrackHealthViewModel

    private val trackFragment = TrackFragment()
    private val logFragment = LogFragment()
    private val calendarFragment = CalendarFragment()
    private val medicationFragment = MedicationFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_health)
        replaceFragment(trackFragment)

        viewModel = ViewModelProvider(this).get(TrackHealthViewModel::class.java)

        viewModel.patientId = intent.getIntExtra("patient_id", -1)
        Log.d("TrackHealth","Patient ID: ${viewModel.patientId}")

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.ic_dashboard -> replaceFragment(trackFragment)
                R.id.ic_log -> replaceFragment(logFragment)
                R.id.ic_calendar -> replaceFragment(calendarFragment)
                R.id.ic_medication -> replaceFragment(medicationFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        if(fragment != null) {
            val transcation = supportFragmentManager.beginTransaction()
            transcation.replace(R.id.fragment_container, fragment)
            transcation.commit()
        }
    }
}