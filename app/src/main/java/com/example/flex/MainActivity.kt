package com.example.flex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    var account=AccountFragment()
    var home=HomeFragment()
    var tv=TvFragment()
    var map=MapFragment()
    var camera=CameraFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, home,"fragment_tag")
            .commit()
        setActionListener()
    }

    private fun setActionListener() {
        val bnv = findViewById<BottomNavigationView>(R.id.bottom_bar)
        bnv.setOnNavigationItemSelectedListener{ menuItem ->
            //here code in case of choosing item in bottom bar
            var selectedFragment: Fragment
            when (menuItem.itemId) {
                R.id.action_account -> {
                    if(supportFragmentManager.findFragmentByTag("fragment_tag")!!.equals(account))
                    account=AccountFragment()
                selectedFragment=account }
                R.id.action_camera -> {
                    if(supportFragmentManager.findFragmentByTag("fragment_tag")!!.equals(camera))
                    camera=CameraFragment()
                selectedFragment=camera }
                R.id.action_home -> {
                    if(supportFragmentManager.findFragmentByTag("fragment_tag")!!.equals(home))
                    home=HomeFragment()
                    selectedFragment=home }
                R.id.action_map -> {
                    if(supportFragmentManager.findFragmentByTag("fragment_tag")!!.equals(map))
                    map=MapFragment()
                    selectedFragment=map }
                R.id.action_tv -> {
                    if(supportFragmentManager.findFragmentByTag("fragment_tag")!!.equals(tv))
                    tv=TvFragment()
                    selectedFragment=tv }
                else->selectedFragment= getSupportFragmentManager().findFragmentById(R.id.frame_container)!!
            }
            supportFragmentManager.beginTransaction().replace(R.id.frame_container, selectedFragment,"fragment_tag").commit()
            true
        }
    }
}
