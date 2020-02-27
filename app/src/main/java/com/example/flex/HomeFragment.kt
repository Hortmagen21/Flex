package com.example.flex

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    lateinit var button:Button
    lateinit var button2:Button
    lateinit var v:View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v=inflater.inflate(R.layout.fragment_home,container,false)
        button=v.findViewById(R.id.button)
        button.setOnClickListener {
            startActivity(Intent(v.context,Registration::class.java))
        }
        button2=v.findViewById(R.id.button2)
        button2.setOnClickListener {
            startActivity(Intent(v.context,SignIn::class.java))
        }
        return v
    }
}