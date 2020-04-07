package com.example.flex.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.Adapter.PhotosAdapter
import com.example.flex.MainData
import com.example.flex.POJO.Post
import com.example.flex.POJO.User
import com.example.flex.R
import com.example.flex.Requests.UsersRequests

class AccountPostTableRecyclerFragment(private val activity:AppCompatActivity,private val user:User?): Fragment() {
    lateinit var v: View
    private lateinit var recycler: RecyclerView
    lateinit var adapter: PhotosAdapter
    private var request:UsersRequests?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.account_post_table_recycler, container, false)
        loadRecycler()
        addActionListener()
        loadPhotos()
        return v
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (request != null) {
            request!!.stopRequests()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (request != null) {
            request!!.stopRequests()
        }
    }

    override fun onPause() {
        super.onPause()
        if (request != null) {
            request!!.stopRequests()
        }
    }

    fun addActionListener() {

    }
    private fun loadRecycler() {
        recycler = v.findViewById(R.id.recycler_photos_account)
        recycler.layoutManager = GridLayoutManager(this.context, 3)

        adapter = PhotosAdapter(this)
        recycler.adapter = adapter
    }
    fun loadPhotos(){
        request=makeUserRequest()
        if(user!=null){
            request!!.viewAcc(user.id)
        }else{
            val sharedPreferences=activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
            request!!.viewAcc(sharedPreferences.getLong(MainData.YOUR_ID,0))
        }
    }
    private fun makeUserRequest(): UsersRequests {
        val activity = this.activity
        val sharedPreferences =
            activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        val sessionId = sharedPreferences.getString(MainData.SESION_ID, "")
        val csrftoken = sharedPreferences.getString(MainData.CRSFTOKEN, "")
        return UsersRequests(this,csrftoken, sessionId)
    }
    fun addPhotos(list:List<Post>) {
        adapter.addPhotos(list)
    }
}