package com.example.flex.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.POJO.Post
import com.example.flex.POJO.User
import com.example.flex.R
import com.squareup.picasso.Picasso

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private var onUserClickListener:PostViewHolder.OnUserClickListener
    private var postList = mutableListOf<Post>()
    constructor(onUserClickListener: PostViewHolder.OnUserClickListener){
        this.onUserClickListener=onUserClickListener
    }

    class PostViewHolder : RecyclerView.ViewHolder {
        private var onUserClickListener:OnUserClickListener
        private var mainUserAvatar: ImageView
        private var mainUserName: TextView
        private var postImage: ImageView
        private var commentatorAvatar: ImageView
        private var commentatorName: TextView
        private var commentText: TextView
        private var firesCount: TextView
        private var commentsCount: TextView
        private var shareCount: TextView
        private lateinit var post:Post

        constructor(v: View,onUserClickListener: OnUserClickListener) : super(v) {
            this.onUserClickListener=onUserClickListener
            mainUserAvatar = v.findViewById(R.id.user_icon)
            mainUserName = v.findViewById(R.id.user_name)
            postImage = v.findViewById(R.id.main_image)
            commentatorAvatar = v.findViewById(R.id.user_comment_icon)
            commentatorName = v.findViewById(R.id.user_comment_name)
            commentText = v.findViewById(R.id.comment_text)
            firesCount = v.findViewById(R.id.fire_count)
            commentsCount = v.findViewById(R.id.comments_count)
            shareCount = v.findViewById(R.id.share_count)
            mainUserAvatar.setOnClickListener {
                onUserClickListener.onUserClick(post.mainUser)
            }
            commentatorAvatar.setOnClickListener {
                onUserClickListener.onUserClick(post.commentUser)
            }
        }

        fun bind(post: Post) {
            this.post=post
            mainUserName.text = post.mainUser.name
            Picasso.get().load(post.mainUser.imageUrl).into(mainUserAvatar)
            Picasso.get().load(post.imageUrl).into(postImage)
            Picasso.get().load(post.commentUser.imageUrl).into(commentatorAvatar)
            commentatorName.text = post.commentUser.name
            commentText.text = post.commentText
            firesCount.text = post.countOfFires.toString()
            commentsCount.text = post.countOfComments.toString()
            shareCount.text = post.countOfShares.toString()
        }
        interface OnUserClickListener {
            fun onUserClick(user: User)
        }
    }

    fun addItems(posts: Collection<Post>) {
        postList.addAll(posts)
        notifyDataSetChanged()
    }

    fun clearItems() {
        postList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.published_photo_layout, parent, false)

        return PostViewHolder(view,onUserClickListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(postList.get(position))
    }

    override fun getItemCount(): Int {
        return postList.size
    }


}