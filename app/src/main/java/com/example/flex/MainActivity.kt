package com.example.flex

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flex.Fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    var account = AccountFragment()
    var home = HomeFragment()
    var tv = TvFragment()
    var map = MapFragment()
    var camera = CameraFragment()
    lateinit var bnv: BottomNavigationView
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val action = intent?.action
        val data = intent?.data
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, home, "fragment_tag")
            .commit()
        setActionListener()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            if (supportFragmentManager.findFragmentByTag("fragment_tag") != camera) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, camera, "fragment_tag")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
    }

    private fun setActionListener() {
        bnv = findViewById(R.id.bottom_bar)
        bnv.setOnNavigationItemSelectedListener { menuItem ->
            //here code in case of choosing item in bottom bar
            var isAddToBackStack = true
            val selectedFragment: Fragment =
                when (menuItem.itemId) {
                    R.id.action_account -> {
                        if (supportFragmentManager.findFragmentByTag("fragment_tag")!! == account) {
                            account = AccountFragment()
                            isAddToBackStack = false
                        }
                        account.activity = this
                        account
                    }
                    R.id.action_camera -> {
                        if (supportFragmentManager.findFragmentByTag("fragment_tag")!! == camera) {
                            camera = CameraFragment()
                            isAddToBackStack = false
                        }
                        camera
                    }
                    R.id.action_home -> {
                        if (supportFragmentManager.findFragmentByTag("fragment_tag")!! == home) {
                            home = HomeFragment()
                            isAddToBackStack = false
                        }
                        home
                    }
                    R.id.action_map -> {
                        if (supportFragmentManager.findFragmentByTag("fragment_tag")!! == map) {
                            map = MapFragment()
                            isAddToBackStack = false
                        }
                        map
                    }
                    R.id.action_tv -> {
                        if (supportFragmentManager.findFragmentByTag("fragment_tag")!! == tv) {
                            tv = TvFragment()
                            isAddToBackStack = false
                        }
                        tv
                    }
                    else -> supportFragmentManager.findFragmentById(R.id.frame_container)!!
                }
            val fragmentManager = supportFragmentManager.beginTransaction()
            fragmentManager.replace(R.id.frame_container, selectedFragment, "fragment_tag")
            if (isAddToBackStack) fragmentManager.addToBackStack(null)
            fragmentManager.commit()
            true
        }
    }
}
