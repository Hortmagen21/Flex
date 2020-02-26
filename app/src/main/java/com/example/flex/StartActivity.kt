package com.example.flex

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class StartActivity :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.frame_container,HomeFragment()).commit()
        setActionListener()
    }
    fun setActionListener(){
        bottom_bar.setOnClickListener{
            supportFragmentManager.beginTransaction().replace(R.id.frame_container,
                    when(bottom_bar.selectedItemId){
                    R.id.action_account->AccountFragment()
                    R.id.action_camera->CameraFragment()
                    R.id.action_home->HomeFragment()
                    R.id.action_map->MapFragment()
                    R.id.action_tv->TvFragment()
                    else->HomeFragment()}).commit()
        }
    }
}