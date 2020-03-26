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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.Adapter.PhotosAdapter
import com.example.flex.MainData
import com.example.flex.POJO.User
import com.example.flex.R
import com.example.flex.Requests.UsersRequests
import com.squareup.picasso.Picasso

class AccountFragment : Fragment() {
    private lateinit var avatar: ImageView
    private lateinit var followedCount: TextView
    private lateinit var followersCount: TextView
    private var user: User? = null
    private lateinit var v: View
    lateinit var activity: AppCompatActivity
    lateinit var followBtn: Button
    private var isI: Boolean = false
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: PhotosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_account, container, false)
        avatar = v.findViewById(R.id.user_icon_main)
        followedCount = v.findViewById(R.id.followed_count)
        followersCount = v.findViewById(R.id.followers_count)
        if (user != null) {
            if (user!!.imageUrl != "") Picasso.get().load(user!!.imageUrl).into(avatar)
            followersCount.text = user!!.followersCount.toString()
            followedCount.text = user!!.followingCount.toString()
        }
        loadRecycler()
        loadPhotos()
        addActionListener()
        if (user == null) {
            getCountOfFoll(0)
            yesI()
        } else {
            getCountOfFoll(user!!.id)
            checkForI(user!!.id)
        }
        return v
    }

    private fun loadPhotos() {
        adapter.addPhotos(getPhotos())
    }

    private fun getPhotos(): List<String> {
        return listOf(
            "https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80",
            "https://static.toiimg.com/photo/72975551.cms",
            "https://images.pexels.com/photos/34950/pexels-photo.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500",
            "https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg",
            "https://www.freedigitalphotos.net/images/img/homepage/394230.jpg",
            "https://media.gettyimages.com/photos/beautiful-book-picture-id865109088?s=612x612",
            "https://media3.s-nbcnews.com/j/newscms/2019_41/3047866/191010-japan-stalker-mc-1121_06b4c20bbf96a51dc8663f334404a899.fit-760w.JPG",
            "https://images.unsplash.com/photo-1503803548695-c2a7b4a5b875?ixlib=rb-1.2.1&w=1000&q=80"
        )
    }

    private fun loadRecycler() {
        recycler = v.findViewById(R.id.recycler_photos_account)
        recycler.layoutManager = GridLayoutManager(this.context, 3)

        adapter = PhotosAdapter()
        recycler.adapter = adapter
    }

    private fun addActionListener() {
        followBtn = v.findViewById(R.id.button_follow)
        followBtn.setOnClickListener {
            val request = UsersRequests(this)
            val activity = v.context as AppCompatActivity
            val sharedPreferences =
                activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
            request.sessionId = sharedPreferences.getString(MainData().SESION_ID, "")
            request.csrftoken = sharedPreferences.getString(MainData().CRSFTOKEN, "")
            request.follow(user?.id)
        }

    }

    fun setUser(user: User) {
        this.user = user
    }

    fun nullSession(activity: AppCompatActivity) {
        val sharedPreferences = activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(MainData().CRSFTOKEN, "")
        editor.putString(MainData().SESION_ID, "")
        editor.apply()
    }

    fun followed() {
        followBtn.text = "Unfollow"
        followBtn.setOnClickListener {

        }
    }

    private fun getCountOfFoll(id: Long) {
        val request = UsersRequests(this)
        val activity = v.context as AppCompatActivity
        val sharedPreferences =
            activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        request.sessionId = sharedPreferences.getString(MainData().SESION_ID, "")
        request.csrftoken = sharedPreferences.getString(MainData().CRSFTOKEN, "")
        request.getFollowersCount(id)
        request.getFollowingCount(id)
    }

    private fun checkForI(userId: Long) {
        val request = UsersRequests(this)
        val activity = v.context as AppCompatActivity
        val sharedPreferences =
            activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        request.sessionId = sharedPreferences.getString(MainData().SESION_ID, "")
        request.csrftoken = sharedPreferences.getString(MainData().CRSFTOKEN, "")
        request.viewAcc(userId)
    }

    fun notI() {
        isI = false
    }

    fun yesI() {
        isI = true
    }
}

