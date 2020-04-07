package com.example.flex.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.flex.Adapter.ViewPagerAdapter
import com.example.flex.MainData
import com.example.flex.POJO.User
import com.example.flex.R
import com.example.flex.Requests.UsersRequests
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso

class AccountFragment(private val activity: AppCompatActivity) : Fragment() {
    lateinit var avatar: ImageView
    private lateinit var followedCount: TextView
    private lateinit var followersCount: TextView
    var user: User? = null
    private lateinit var v: View
    lateinit var followBtn: Button
    private var isI: Boolean = false
    private lateinit var tableRecyclerView: AccountPostTableRecyclerFragment
    private lateinit var listRecyclerView: AccountPostListRecyclerFragment
    private lateinit var currentRecycler: Fragment
    private lateinit var switchTab: TabLayout
    private lateinit var viewPager: ViewPager
    private var request: UsersRequests? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_account, container, false)
        avatar = v.findViewById(R.id.user_icon_main)
        followedCount = v.findViewById(R.id.followed_count)
        followersCount = v.findViewById(R.id.followers_count)
        tableRecyclerView = AccountPostTableRecyclerFragment(activity, user)
        listRecyclerView = AccountPostListRecyclerFragment(activity, user)
        if (user != null) {
            if (user!!.imageUrl != "") Picasso.get().load(user!!.imageUrl).into(avatar)
            followersCount.text = user!!.followersCount.toString()
            followedCount.text = user!!.followingCount.toString()
        }
        addActionListener()
        if (user == null) {
            getCountOfFoll(0)
            yesI()
        } else {
            getCountOfFoll(user!!.id)
            //checkForI(user!!.id)
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        tableRecyclerView = AccountPostTableRecyclerFragment(activity, user)
        listRecyclerView = AccountPostListRecyclerFragment(activity, user)
        if (user != null) {
            if (user!!.imageUrl != "") Picasso.get().load(user!!.imageUrl).into(avatar)
            followersCount.text = user!!.followersCount.toString()
            followedCount.text = user!!.followingCount.toString()
        }
        addActionListener()
        if (user == null) {
            getCountOfFoll(0)
            yesI()
        } else {
            getCountOfFoll(user!!.id)
            //checkForI(user!!.id)
        }
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
        if(request!=null){
            request!!.stopRequests()
        }
    }

    private fun addActionListener() {
        switchTab = v.findViewById(R.id.switchRecyclers)
        viewPager = v.findViewById(R.id.recycler_fragment)
        val viewPagerAdapter = fragmentManager?.let { ViewPagerAdapter(it) }
        viewPagerAdapter!!.addFragment(tableRecyclerView, "Grid")
        viewPagerAdapter.addFragment(listRecyclerView, "List")
        viewPager.adapter = viewPagerAdapter
        switchTab.setupWithViewPager(viewPager)
        followBtn = v.findViewById(R.id.button_follow)
        followBtn.setOnClickListener {
            val request = makeUserRequest()
            request.follow(user?.id)
        }

    }

    fun nullSession(activity: AppCompatActivity) {
        val sharedPreferences =
            activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(MainData.CRSFTOKEN, "")
        editor.putString(MainData.SESION_ID, "")
        editor.apply()
    }

    fun followed() {
        followBtn.text = "Unfollow"
        followBtn.setOnClickListener {

        }
    }

    private fun getCountOfFoll(id: Long) {
        val request = makeUserRequest()
        request.getFollowersCount(id)
        request.getFollowingCount(id)
    }

    private fun checkForI(userId: Long) {
        request = makeUserRequest()
        request!!.viewAcc(userId)
    }

    fun notI() {
        isI = false
    }

    fun yesI() {
        isI = true
    }

    private fun makeUserRequest(): UsersRequests {
        val activity = v.context as AppCompatActivity
        val sharedPreferences =
            activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        val sessionId = sharedPreferences.getString(MainData.SESION_ID, "")
        val csrftoken = sharedPreferences.getString(MainData.CRSFTOKEN, "")
        return UsersRequests(this, csrftoken, sessionId)
    }

}

