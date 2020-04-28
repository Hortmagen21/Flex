package com.example.flex.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.Adapter.PhotosAdapter
import com.example.flex.POJO.PostAccount
import com.example.flex.POJO.User
import com.example.flex.R
import com.example.flex.AccountViewModel
import com.example.flex.MainData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountPostTableRecyclerFragment(private var user: User?, val updator: UserUpdates) :
    Fragment(), PhotosAdapter.PhotosDownload {
    lateinit var v: View
    private lateinit var recycler: RecyclerView
    lateinit var adapter: PhotosAdapter
    private lateinit var mAccountViewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.account_post_table_recycler, container, false)
        if (user == null) {
            val sharedPreferences =
                v.context.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
            user = User(sharedPreferences.getLong(MainData.YOUR_ID, 0))
        }
        loadRecycler()
        mAccountViewModel = ViewModelProviders.of(activity!!).get(AccountViewModel::class.java)
        mAccountViewModel.getAllPostsAccount(user!!.id).observe(viewLifecycleOwner, Observer {
            adapter.setPhotos(it)

        })
        mAccountViewModel.getMainUser().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (user == null || user!!.id == it.id) {
                    user = it
                    updator.setUser(it)
                }
            }
        })
        addActionListener()
        loadPhotos()
        return v
    }

    fun addActionListener() {

    }

    private fun loadRecycler() {
        recycler = v.findViewById(R.id.recycler_photos_account)
        recycler.layoutManager = GridLayoutManager(this.context, 3)

        adapter = PhotosAdapter(this)
        recycler.adapter = adapter
    }

    private fun loadPhotos() {
        mAccountViewModel.getMiniPostsForAcc(user!!.id, user)
    }

    fun addPhotos(list: List<PostAccount>) {
        adapter.addPhotos(list)
    }

    interface UserUpdates {
        fun setUser(user: User)
    }

    override fun downloadPhoto(link: String, photo: ImageView) {
        mAccountViewModel.downloadPhoto(link, photo)
    }
}