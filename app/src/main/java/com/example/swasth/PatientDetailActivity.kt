package com.example.swasth

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.swasth.fragments.BookAppointmentFragment
import com.example.swasth.fragments.HomeFragment
import com.example.swasth.fragments.LocationFragment
import com.example.swasth.fragments.MedicationFragment
import com.example.swasth.fragments.UserFragment
import com.nafis.bottomnavigation.NafisBottomNavigation

class PatientDetailActivity : AppCompatActivity() {

    companion object {
        private const val ID_LOCATION = 1
        private const val ID_MEDICATION = 2
        private const val ID_HOME = 3
        private const val ID_HISTORY = 4
        private const val ID_USER = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_patientdetail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigation: NafisBottomNavigation = findViewById(R.id.bottomNavigation)

        bottomNavigation.add(NafisBottomNavigation.Model(ID_LOCATION, R.drawable.location))
        bottomNavigation.add(NafisBottomNavigation.Model(ID_MEDICATION, R.drawable.medicine))
        bottomNavigation.add(NafisBottomNavigation.Model(ID_HOME, R.drawable.home))
        bottomNavigation.add(NafisBottomNavigation.Model(ID_HISTORY, R.drawable.history))
        bottomNavigation.add(NafisBottomNavigation.Model(ID_USER, R.drawable.user))

        // Show default fragment
        loadFragment(HomeFragment())
        bottomNavigation.show(ID_HOME)

        // Handle item selection
        bottomNavigation.setOnClickMenuListener { item ->
            Log.d("BottomNav", "Clicked item ID: ${item.id}") // Debugging log

            val fragment = when (item.id) {
                ID_LOCATION -> LocationFragment()
                ID_MEDICATION -> MedicationFragment()
                ID_HOME -> HomeFragment()
                ID_HISTORY -> BookAppointmentFragment()
                ID_USER -> UserFragment()
                else -> HomeFragment()
            }

            loadFragment(fragment)
        }

    }

    private fun loadFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (currentFragment != null && currentFragment::class == fragment::class) {
            Log.d("Fragment", "Same fragment already loaded, skipping replacement")
            return // Prevent unnecessary reloads
        }

        Log.d("Fragment", "Replacing with ${fragment::class.java.simpleName}")

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commitNow()
    }
}