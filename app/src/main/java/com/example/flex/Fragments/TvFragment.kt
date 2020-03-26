package com.example.flex.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.Adapter.SearchAdapter
import com.example.flex.MainData
import com.example.flex.POJO.User
import com.example.flex.R
import com.example.flex.Requests.SearchRequests

class TvFragment : Fragment(), SearchAdapter.OnUserClickListener, SearchView.OnQueryTextListener {
    lateinit var v: View
    lateinit var recycler: RecyclerView
    lateinit var searchAdapter: SearchAdapter
    lateinit var search: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_tv, container, false)
        addActionListener()
        loadRecyclerView()
        loadUsers()
        return v
    }

    private fun addActionListener() {
        search = v.findViewById(R.id.search_search)
        search.setOnQueryTextListener(this)
    }

    private fun getUsers(): List<User> {
        return listOf(
            User(
                2,
                "DeLitX",
                "https://images.pexels.com/photos/34950/pexels-photo.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                235,
                523
            ),
            User(
                1,
                "Vlad",
                "https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80",
                325,
                235
            ),
            User(
                3,
                "Vilard",
                "https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg",
                467,
                26
            ),
            User(
                4,
                "Hortmagen",
                "https://media.gettyimages.com/photos/beautiful-book-picture-id865109088?s=612x612",
                6236,
                2636
            ),
            User(6, "", "", 2362, 237)
        )
    }

    private fun loadUsers() {
        searchAdapter.addUsers(null)
    }

    private fun loadRecyclerView() {
        recycler = v.findViewById(R.id.recycler_search)
        recycler.layoutManager = LinearLayoutManager(v.context)
        searchAdapter = SearchAdapter(this)
        recycler.adapter = searchAdapter
    }

    override fun onUserClick(user: User) {
        if (user.name != "") {
            val fragment = AccountFragment()
            fragmentManager!!.beginTransaction()
                .replace(R.id.frame_container, fragment, "fragment_tag").addToBackStack(null)
                .commit()
            fragment.setUser(user)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null && query != "") {
            val request = SearchRequests(this)
            val activity = v.context as AppCompatActivity
            var sharedPreferences =
                activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
            request.sessionId = sharedPreferences.getString(MainData().SESION_ID, "")
            request.csrftoken = sharedPreferences.getString(MainData().CRSFTOKEN, "")
            request.search(query, searchAdapter)
            return true
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null && newText != "") {
            val request = SearchRequests(this)
            val activity = v.context as AppCompatActivity
            var sharedPreferences =
                activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
            request.sessionId = sharedPreferences.getString(MainData().SESION_ID, "")
            request.csrftoken = sharedPreferences.getString(MainData().CRSFTOKEN, "")
            request.search(newText, searchAdapter)
            return true
        }
        return false
    }
}