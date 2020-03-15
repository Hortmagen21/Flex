package com.example.flex.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flex.R

class CameraFragment : Fragment() {
    lateinit var v:View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v=inflater.inflate(R.layout.fragment_photo,container,false)
        addActionListener()
        return v
    }
    fun addActionListener(){

    }
}