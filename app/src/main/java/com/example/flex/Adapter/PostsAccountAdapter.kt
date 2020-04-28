package com.example.flex.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.POJO.Post
import com.example.flex.POJO.PostAccount
import com.example.flex.R

class PostsAccountAdapter(
    private val mOnPostClickListener: OnPostClickListener,
    private val mPhotosDownload: PhotosDownload
) :
    RecyclerView.Adapter<PostsAccountAdapter.PostsViewHolder>() {
    private val posts = mutableListOf<PostAccount>()

    class PostsViewHolder(
        private val v: View,
        private val mOnPostClickListener: OnPostClickListener,
        private val mPhotosDownload: PhotosDownload
    ) :
        RecyclerView.ViewHolder(v) {
        private val mainUserAvatar: ImageView = v.findViewById(R.id.user_icon)
        private val mainUserName: TextView = v.findViewById(R.id.user_name)
        private val postImage: ImageView = v.findViewById(R.id.main_image)
        private val fireIcon: TextView = v.findViewById(R.id.fire_icon)
        private val firesCount: TextView = v.findViewById(R.id.fire_count)
        private val commentsCount: TextView = v.findViewById(R.id.comments_count)
        private val shareCount: TextView = v.findViewById(R.id.share_count)
        private val postText: TextView = v.findViewById(R.id.post_text)
        private lateinit var post: PostAccount
        private var isLiked = false

        init {
            postImage.setOnClickListener {

            }
            fireIcon.setOnClickListener {
                if (!isLiked) {
                    mOnPostClickListener.onLikeClick(post)
                    firesCount.text = (firesCount.text.toString().toLong() + 1).toString()
                    isLiked = true
                } else {
                    mOnPostClickListener.onUnlikeClick(post)
                    firesCount.text = (firesCount.text.toString().toLong() - 1).toString()
                    isLiked = false
                }
            }
        }

        fun bind(post: PostAccount) {
            this.post = post
            isLiked = post.isLiked
            if (post.imageUrl != "") {
                mPhotosDownload.photoDownload(post.imageUrl, postImage)
                mPhotosDownload.photoDownload(post.mainUser.imageUrl, mainUserAvatar)
                mainUserName.text = post.mainUser.name
                commentsCount.text = post.countOfComments.toString()
                postText.text = post.postText
                firesCount.text = post.countOfFires.toString()
                shareCount.text = post.countOfShares.toString()
            } else {
            }
        }
    }

    interface OnPostClickListener {
        fun onLikeClick(post: PostAccount)
        fun onUnlikeClick(post: PostAccount)
        fun onCommentClick(post: PostAccount)
    }

    fun addPosts(list: List<PostAccount>) {
        posts.addAll(list)
        notifyDataSetChanged()
    }

    fun setPosts(list: List<PostAccount>) {
        posts.clear()
        posts.addAll(list)
        notifyDataSetChanged()
    }

    fun clearPosts() {
        posts.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.published_photo_layout, parent, false)
        return PostsViewHolder(v, mOnPostClickListener, mPhotosDownload)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    interface PhotosDownload {
        fun photoDownload(link: String, photo: ImageView)
    }
}