package com.example.flex

import android.content.Context
import android.view.ContextMenu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class PostView(private val view:View): View(view.context) {
    fun setFragment(fragment:Fragment,id:Int) {
        val activity=view.context as AppCompatActivity
        activity.supportFragmentManager.beginTransaction().replace(id,fragment)
    }

}