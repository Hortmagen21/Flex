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
import com.example.flex.Adapter.PostsAccountAdapter
import com.example.flex.MainData
import com.example.flex.POJO.Post
import com.example.flex.POJO.User
import com.example.flex.R
import com.example.flex.Requests.PostRequests

class AccountPostListRecyclerFragment(
    private val activity: AppCompatActivity,
    private val user: User?
) : Fragment() {
    lateinit var v: View
    private lateinit var recycler: RecyclerView
    lateinit var adapter: PostsAccountAdapter
    private var request: PostRequests? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.account_post_list_recycler, container, false)
        loadRecycler()
        addActionListener()
        loadPosts()
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
        recycler = v.findViewById(R.id.recycler_posts_account)
        recycler.layoutManager = LinearLayoutManager(this.context)

        adapter = PostsAccountAdapter(this)
        recycler.adapter = adapter
    }

    fun loadPosts() {
        request = makePostRequest()
        if (user != null) {
            request!!.viewAllPostsAccount(user.id)
        } else {
            val sharedPreferences =
                activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
            request!!.viewAllPostsAccount(sharedPreferences.getLong(MainData.YOUR_ID, 0))
        }
    }

    private fun makePostRequest(): PostRequests {
        val activity = this.activity
        val sharedPreferences =
            activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        val sessionId = sharedPreferences.getString(MainData.SESION_ID, "")
        val csrftoken = sharedPreferences.getString(MainData.CRSFTOKEN, "")
        return PostRequests(this, csrftoken, sessionId)
    }

    fun addPosts(list: List<Post>) {
        adapter.addPosts(list)
    }
}