package com.example.flex.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flex.Fragments.*
import com.example.flex.MainData
import com.example.flex.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    var account = MainUserAccountFragment()
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
        val sharedPreferences = getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        val sessionId = sharedPreferences.getString(MainData.SESSION_ID, "")
        val csrftoken = sharedPreferences.getString(MainData.CRSFTOKEN, "")
        if (sessionId == "" || csrftoken == "") {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
            finish()
        }
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

    private fun setActionListener() {
        bnv = findViewById(R.id.bottom_bar)
        bnv.setOnNavigationItemSelectedListener { menuItem ->
            //here code in case of choosing item in bottom bar
            var isAddToBackStack = true
            val selectedFragment: Fragment =
                when (menuItem.itemId) {
                    R.id.action_account -> {
                        isAddToBackStack = true
                        if (supportFragmentManager.findFragmentByTag("fragment_tag")!! == account) {
                            isAddToBackStack = false
                        }
                        account
                    }
                    R.id.action_camera -> {
                        isAddToBackStack = true
                        if (supportFragmentManager.findFragmentByTag("fragment_tag")!! == camera) {
                            isAddToBackStack = false
                        }
                        camera
                    }
                    R.id.action_home -> {
                        if (supportFragmentManager.findFragmentByTag("fragment_tag")!! == home) {
                            home.scrollToBeginning()
                        }
                        home
                    }
                    R.id.action_map -> {
                        isAddToBackStack = true
                        if (supportFragmentManager.findFragmentByTag("fragment_tag")!! == map) {
                            isAddToBackStack = false
                        }
                        map
                    }
                    R.id.action_tv -> {
                        isAddToBackStack = true
                        if (supportFragmentManager.findFragmentByTag("fragment_tag")!! == tv) {
                            isAddToBackStack = false
                        }
                        tv
                    }
                    else -> supportFragmentManager.findFragmentById(R.id.frame_container)!!
                }
            if (isAddToBackStack) {
                val fragmentManager = supportFragmentManager.beginTransaction()
                fragmentManager.replace(R.id.frame_container, selectedFragment, "fragment_tag")
                if (isAddToBackStack) fragmentManager.addToBackStack(null)
                fragmentManager.commit()
            }
            true
        }
    }
}
