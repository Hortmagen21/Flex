package com.example.flex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, HomeFragment())
            .commit()
        setActionListener()
    }

    private fun setActionListener() {
        val bnv = findViewById<BottomNavigationView>(R.id.bottom_bar)
        bnv.setOnNavigationItemSelectedListener{ menuItem ->
            //here code in case of choosing item in bottom bar
            var selectedFragment: Fragment
            when (menuItem.itemId) {
                R.id.action_account -> selectedFragment = AccountFragment()
                R.id.action_camera -> selectedFragment = CameraFragment()
                R.id.action_home -> selectedFragment = HomeFragment()
                R.id.action_map -> selectedFragment = MapFragment()
                R.id.action_tv -> selectedFragment = TvFragment()
                else->selectedFragment=HomeFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, selectedFragment).commit()
            true
        }
    }
}
