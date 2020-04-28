package com.example.flex.Fragments

import android.content.Context
import android.content.Intent
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
import com.example.flex.*
import com.example.flex.Adapter.PostAdapter
import com.example.flex.POJO.Post
import com.example.flex.POJO.User

class HomeFragment : Fragment(), PostAdapter.OnUserClickListener,PostAdapter.PhotosDownload {
    lateinit var v: View
    lateinit var recycler: RecyclerView
    lateinit var postAdapter: PostAdapter
    private lateinit var mViewModel:HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_home, container, false)
        mViewModel= ViewModelProviders.of(this).get(HomeViewModel::class.java)
        mViewModel.allPosts.observe(viewLifecycleOwner, Observer {
            setPosts(it)
        })
        mViewModel.isMustSignIn.observe(viewLifecycleOwner, Observer {
            if(it==true){
                val intent= Intent(this.context, SignIn::class.java)
                startActivity(intent)
                activity?.finish()
            }
        })
        loadRecyclerView()
        loadPosts()
        return v
    }

    private fun loadPosts() {
        mViewModel.refreshPosts(0)
    }

    private fun setPosts(list: List<Post>) {
        postAdapter.setItems(list)
    }

    private fun loadRecyclerView() {
        recycler = v.findViewById(R.id.main_recycler_view)
        recycler.layoutManager = LinearLayoutManager(v.context)

        postAdapter = PostAdapter(this, this)
        recycler.adapter = postAdapter
    }

    fun scrollToBeginning() {
        recycler.smoothScrollToPosition(0)
    }

    override fun onUserClick(user: User) {
        val sharedPreferences =
            v.context.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        if (user.id == sharedPreferences.getLong(MainData.YOUR_ID, 0)||user.id==0.toLong()) {
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

    override fun onLikeClick(post:Post) {
        mViewModel.likePost(post)
    }

    override fun onCommentClick(postId:Long,text:String) {
        val intent=Intent(this.context,CommentsEnlist::class.java)
        intent.putExtra("PostId",postId)
        startActivity(intent)
    }

    override fun onUnlikeClick(post:Post) {
        mViewModel.unLikePost(post)
    }


    override fun photoDownload(link: String, photo: ImageView) {
        mViewModel.downloadPhoto(link,photo)
    }
}