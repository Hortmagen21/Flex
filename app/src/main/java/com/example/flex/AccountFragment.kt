package com.example.flex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AccountFragment : Fragment() {
    lateinit var v:View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v=inflater.inflate(R.layout.fragment_account,container,false)
        return v
    }
}