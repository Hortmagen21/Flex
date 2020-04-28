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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.AccountViewModel
import com.example.flex.Adapter.PostsAccountAdapter
import com.example.flex.MainData
import com.example.flex.POJO.PostAccount
import com.example.flex.POJO.User
import com.example.flex.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountPostListRecyclerFragment(
    private var user: User?
) : Fragment(), PostsAccountAdapter.OnPostClickListener, PostsAccountAdapter.PhotosDownload {
    lateinit var v: View
    private lateinit var recycler: RecyclerView
    lateinit var adapter: PostsAccountAdapter
    private lateinit var mAccountViewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.account_post_list_recycler, container, false)
        if (user == null) {
            val sharedPreferences =
                v.context.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
            user = User(sharedPreferences.getLong(MainData.YOUR_ID, 0))
        }
        loadRecycler()
        mAccountViewModel = ViewModelProviders.of(activity!!).get(AccountViewModel::class.java)
        mAccountViewModel.getAllPostsAccount(user!!.id).observe(viewLifecycleOwner, Observer {
            adapter.setPosts(it)


        })
        addActionListener()
        loadPosts()
        return v
    }

    private fun addActionListener() {

    }

    private fun loadRecycler() {
        recycler = v.findViewById(R.id.recycler_posts_account)
        recycler.layoutManager = LinearLayoutManager(this.context)

        adapter = PostsAccountAdapter(this, this)
        recycler.adapter = adapter
    }

    private fun loadPosts() {
        mAccountViewModel.getPostsForAcc(user!!.id)
    }

    fun addPosts(list: List<PostAccount>) {
        adapter.addPosts(list)
    }

    override fun onLikeClick(post: PostAccount) {
        mAccountViewModel.likePost(post)
    }

    override fun onCommentClick(post: PostAccount) {
        mAccountViewModel.unLikePost(post)
    }

    override fun onUnlikeClick(post: PostAccount) {
        //TODO
    }

    override fun photoDownload(link: String, photo: ImageView) {
        mAccountViewModel.downloadPhoto(link, photo)
    }
}