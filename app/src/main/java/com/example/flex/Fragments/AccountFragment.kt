package com.example.flex.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.flex.*
import com.example.flex.Adapter.ViewPagerAdapter
import com.example.flex.POJO.User
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountFragment : Fragment(),
    AccountPostTableRecyclerFragment.UserUpdates {
    private lateinit var activity: AppCompatActivity
    lateinit var avatar: ImageView
    private lateinit var followingCount: TextView
    private lateinit var followersCount: TextView
    var mUser: User? = null
    private lateinit var v: View
    lateinit var followBtn: Button
    private lateinit var tableRecyclerView: AccountPostTableRecyclerFragment
    private lateinit var listRecyclerView: AccountPostListRecyclerFragment
    private lateinit var currentRecycler: Fragment
    private lateinit var switchTab: TabLayout
    private lateinit var viewPager: ViewPager
    lateinit var userName: TextView
    private lateinit var mAccountViewModel: AccountViewModel
    private lateinit var mLiveAccountUser: LiveData<User>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_account, container, false)
        activity = v.context as AppCompatActivity

        mAccountViewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        mAccountViewModel.isMustSignIn.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val intent = Intent(v.context, SignIn::class.java)
                startActivity(intent)
                activity.finish()
            }
        })
        mAccountViewModel.refreshUser(mUser)
        CoroutineScope(IO).launch {
            mLiveAccountUser = mAccountViewModel.getAccountUser(
                if (mUser == null) {
                    0
                } else {
                    mUser!!.id
                }
            )
            withContext(Main) {
                addActionListener()
                mLiveAccountUser.observe(viewLifecycleOwner, Observer {
                    setUser(it)
                })
                mAccountViewModel.refreshUser(mUser)

                if (mUser!!.imageUrl != "") mAccountViewModel.downloadPhoto(
                    mUser!!.imageUrl,
                    avatar
                )
                followersCount.text = mUser!!.followersCount.toString()
                followingCount.text = mUser!!.followingCount.toString()
            }
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        addActionListener()
    }

    private fun addActionListener() {
        userName = v.findViewById(R.id.user_name)
        avatar = v.findViewById(R.id.user_icon_main)
        followingCount = v.findViewById(R.id.followed_count)
        followersCount = v.findViewById(R.id.followers_count)
        tableRecyclerView = AccountPostTableRecyclerFragment(mUser, this)
        listRecyclerView = AccountPostListRecyclerFragment(mUser)
        if (mUser != null) {
            if (mUser!!.imageUrl != "") Picasso.get().load(mUser!!.imageUrl).into(avatar)
            followersCount.text = mUser!!.followersCount.toString()
            followingCount.text = mUser!!.followingCount.toString()
        }
        switchTab = v.findViewById(R.id.switchRecyclers)
        viewPager = v.findViewById(R.id.recycler_fragment)
        val viewPagerAdapter = fragmentManager?.let { ViewPagerAdapter(it) }
        viewPagerAdapter!!.addFragment(tableRecyclerView, "Grid")
        viewPagerAdapter.addFragment(listRecyclerView, "List")
        viewPager.adapter = viewPagerAdapter
        switchTab.setupWithViewPager(viewPager)
        followBtn = v.findViewById(R.id.button_follow)
        followBtn.text = if (mUser!!.isSubscribed) {
            "Unfollow"
        } else {
            "Follow"
        }
        followBtn.setOnClickListener(
            if (mUser!!.isSubscribed) {
                follow()
            } else {
                unfollow()
            }
        )
    }

    private fun follow(): View.OnClickListener {
        followBtn.text = "Unfollow"
        followersCount.text = (followersCount.text.toString().toLong() + 1).toString()
        mAccountViewModel.follow(mUser!!.id)
        return View.OnClickListener {
            followBtn.setOnClickListener(unfollow())
        }
    }

    private fun unfollow(): View.OnClickListener {
        followBtn.text = "Follow"
        followersCount.text = (followersCount.text.toString().toLong() - 1).toString()
        mAccountViewModel.unfollow(mUser!!.id)
        return View.OnClickListener {
            followBtn.setOnClickListener(follow())
        }
    }

    override fun setUser(user: User) {
        mUser = user
        userName.text = user.name
        followingCount.text = user.followingCount.toString()
        followersCount.text = user.followersCount.toString()
        mAccountViewModel.downloadPhoto(user.imageUrl, avatar)
    }
}

