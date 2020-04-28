package com.example.flex.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.Adapter.SearchAdapter
import com.example.flex.MainData
import com.example.flex.POJO.User
import com.example.flex.R
import com.example.flex.SearchViewModel
import com.example.flex.SignIn

class SearchRecyclerFragment : Fragment(), SearchAdapter.OnUserClickListener {

    lateinit var recycler: RecyclerView
    lateinit var searchAdapter: SearchAdapter
    lateinit var v: View
    private lateinit var mViewModel: SearchViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_search_recycler, container, false)
        mViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        mViewModel.searchResult.observe(viewLifecycleOwner, Observer {
            searchAdapter.setUsers(it)
            //TODO make local pre-search before receiving answer from server
        })
        mViewModel.isMustSignIn.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val intent = Intent(this.context, SignIn::class.java)
                startActivity(intent)
                activity?.finish()
            }
        })
        loadRecyclerView()
        return v
    }

    private fun loadRecyclerView() {
        recycler = v.findViewById(R.id.recycler_search)
        recycler.layoutManager = LinearLayoutManager(v.context)
        searchAdapter = SearchAdapter(this)
        recycler.adapter = searchAdapter
    }

    override fun onUserClick(user: User) {
        if (user.name != "") {
            val sharedPreferences =
                v.context.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
            if (user.id == sharedPreferences.getLong(MainData.YOUR_ID, 0)) {
                val fragment = MainUserAccountFragment()
                fragment.mUser = user
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.frame_container, fragment)?.addToBackStack(null)?.commit()
            } else {
                val fragment = AccountFragment()
                fragment.mUser = user
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.frame_container, fragment, "fragment_tag")?.addToBackStack(null)
                    ?.commit()
            }
        }
    }

    fun requestSearch(text: String) {
        mViewModel.search(text)
    }
}
