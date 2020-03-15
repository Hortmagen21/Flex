package com.example.flex.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.Fragments.CommentFragment
import com.example.flex.POJO.Post
import com.example.flex.POJO.User
import com.example.flex.PostView
import com.example.flex.R
import com.squareup.picasso.Picasso

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private var onUserClickListener: OnUserClickListener
    private var postList = mutableListOf<Post>()

    constructor(onUserClickListener: OnUserClickListener) {
        this.onUserClickListener = onUserClickListener
    }

    class PostViewHolder(
        private val v: View,
        private var onUserClickListener: OnUserClickListener
    ) :
        RecyclerView.ViewHolder(v), CommentFragment.onCommentClickListener {
        private val mainUserAvatar: ImageView = v.findViewById(R.id.user_icon)
        private val mainUserName: TextView = v.findViewById(R.id.user_name)
        private val postImage: ImageView = v.findViewById(R.id.main_image)
        private val firesCount: TextView = v.findViewById(R.id.fire_count)
        private val commentsCount: TextView = v.findViewById(R.id.comments_count)
        private val shareCount: TextView = v.findViewById(R.id.share_count)
        private val postText: TextView = v.findViewById(R.id.post_text)
        private val fragmentContainer = v.findViewById<FrameLayout>(R.id.comment_frame)
        private lateinit var comment: CommentFragment
        private lateinit var post: Post

        init {
            mainUserAvatar.setOnClickListener {
                onUserClickListener.onUserClick(post.mainUser)
            }
        }

        fun bind(post: Post) {
            this.post = post

            mainUserName.text = post.mainUser.name
            if (post.mainUser.imageUrl != "") Picasso.get().load(post.mainUser.imageUrl)
                .into(mainUserAvatar)
            if (post.imageUrl != "") Picasso.get().load(post.imageUrl).into(postImage)
            if (post.postText != "") {
                postText.text = post.postText
                postText.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            } else postText.height = 0
            firesCount.text = post.countOfFires.toString()
            commentsCount.text = post.countOfComments.toString()
            shareCount.text = post.countOfShares.toString()

            if (post.comment.text != "" && post.mainUser.name != "" && post.mainUser.imageUrl != "") {
                comment = CommentFragment(post.comment, this)
                val activity: AppCompatActivity = v.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.comment_frame, comment).commit()
            }
        }

        override fun onCommentClick() {
            onUserClickListener.onUserClick(post.comment.user)
        }

    }

    interface OnUserClickListener {
        fun onUserClick(user: User)
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
        val inflater = LayoutInflater.from(parent.context)
        val view:View=inflater.inflate(R.layout.published_photo_layout, parent, false)

        return PostViewHolder(view, onUserClickListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount(): Int {
        return postList.size
    }


}