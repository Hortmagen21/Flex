package com.example.flex.Adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.CommentsEnlist
import com.example.flex.Fragments.CommentFragment
import com.example.flex.POJO.Post
import com.example.flex.POJO.User
import com.example.flex.R
import com.squareup.picasso.Picasso

class PostAdapter(private var mOnUserClickListener: OnUserClickListener, private val mDownloadPhoto:PhotosDownload) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private var postList = mutableListOf<Post>()

    class PostViewHolder(
        private val v: View,
        private var mOnUserClickListener: OnUserClickListener,
        private val mDownloadPhoto:PhotosDownload
    ) :
        RecyclerView.ViewHolder(v) {
        private val mainUserAvatar: ImageView = v.findViewById(R.id.user_icon)
        private val mainUserName: TextView = v.findViewById(R.id.user_name)
        private val postImage: ImageView = v.findViewById(R.id.main_image)
        private val firesCount: TextView = v.findViewById(R.id.fire_count)
        private val fireIcon: TextView = v.findViewById(R.id.fire_icon)
        private val commentsCount: TextView = v.findViewById(R.id.comments_count)
        private val commentIcon: TextView = v.findViewById(R.id.comments_icon)
        private val shareCount: TextView = v.findViewById(R.id.share_count)
        private val postText: TextView = v.findViewById(R.id.post_text)
        private val fragmentContainer = v.findViewById<FrameLayout>(R.id.comment_frame)
        private lateinit var comment: CommentFragment
        private lateinit var post: Post
        private var isLiked = false

        init {
            mainUserAvatar.setOnClickListener {
                mOnUserClickListener.onUserClick(post.mainUser)
            }
            fireIcon.setOnClickListener {
                if (!isLiked) {
                    mOnUserClickListener.onLikeClick(post)
                    firesCount.text = (firesCount.text.toString().toLong() + 1).toString()
                    post.isLiked=true
                    post.countOfFires=firesCount.text.toString().toLong()
                    fireIcon.setTextColor(Color.RED)
                    isLiked = true
                } else {
                    mOnUserClickListener.onUnlikeClick(post)
                    firesCount.text = (firesCount.text.toString().toLong() - 1).toString()
                    post.isLiked=false
                    post.countOfFires=firesCount.text.toString().toLong()
                    fireIcon.setTextColor(Color.GRAY)
                    isLiked = false
                }
            }
            commentIcon.setOnClickListener {
                val intent= Intent(v.context,CommentsEnlist::class.java)
                intent.putExtra("PostId",post.id)
                v.context.startActivity(intent)
            }
        }

        fun bind(post: Post) {
            isLiked=post.isLiked
            this.post = post
            mDownloadPhoto.photoDownload(post.imageUrl, postImage)
            mainUserName.text = post.mainUser.name
            if(isLiked){
                fireIcon.setTextColor(Color.RED)
            }else{
                fireIcon.setTextColor(Color.GRAY)
            }
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
            if (post.comment != null && post.mainUser != null)
                if (post.comment!!.text != "" && post.mainUser.name != "" && post.mainUser.imageUrl != "") {
                    comment = CommentFragment(post.comment!!)
                    val activity: AppCompatActivity = v.context as AppCompatActivity
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.comment_frame, comment).commit()
                }
        }

    }

    interface OnUserClickListener {
        fun onUserClick(user: User)
        fun onLikeClick(post: Post)
        fun onUnlikeClick(post:Post)
        fun onCommentClick(postId: Long,text: String)
    }

    fun addItems(posts: List<Post>) {
        postList.addAll(posts)
        notifyDataSetChanged()
    }
    fun setItems(posts:List<Post>){
        postList.clear()
        postList.addAll(posts)
        notifyDataSetChanged()
    }

    fun clearItems() {
        postList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.published_photo_layout, parent, false)
        return PostViewHolder(view, mOnUserClickListener, mDownloadPhoto)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount(): Int {
        return postList.size
    }
    interface PhotosDownload{
        fun photoDownload(link:String,photo:ImageView)
    }

}