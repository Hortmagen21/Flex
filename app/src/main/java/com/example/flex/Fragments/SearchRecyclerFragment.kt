package com.example.flex.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.Adapter.SearchAdapter
import com.example.flex.MainData
import com.example.flex.POJO.User
import com.example.flex.R
import com.example.flex.Requests.SearchRequests

class SearchRecyclerFragment : Fragment(), SearchAdapter.OnUserClickListener {

    lateinit var recycler: RecyclerView
    lateinit var searchAdapter: SearchAdapter
    lateinit var v: View
    private var request:SearchRequests?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_search_recycler, container, false)
        loadRecyclerView()
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

    private fun loadRecyclerView() {
        recycler = v.findViewById(R.id.recycler_search)
        recycler.layoutManager = LinearLayoutManager(v.context)
        searchAdapter = SearchAdapter(this)
        recycler.adapter = searchAdapter
    }

    override fun onUserClick(user: User) {
        if (user.name != "") {
            val fragment = AccountFragment(v.context as AppCompatActivity)
            fragmentManager!!.beginTransaction()
                .replace(R.id.frame_container, fragment, "fragment_tag").addToBackStack(null)
                .commit()
            fragment.user = user
        }
    }

    fun requestSearch(text:String) {
        request = makeSearchRequest()
        request!!.search(text)
    }

    private fun makeSearchRequest(): SearchRequests {
        val activity = v.context as AppCompatActivity
        val sharedPreferences =
            activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        val sessionId = sharedPreferences.getString(MainData.SESION_ID, "")
        val csrftoken = sharedPreferences.getString(MainData.CRSFTOKEN, "")
        return SearchRequests(this, csrftoken, sessionId)
    }
}
