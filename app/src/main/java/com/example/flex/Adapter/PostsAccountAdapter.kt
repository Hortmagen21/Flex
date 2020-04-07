package com.example.flex.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.MainData
import com.example.flex.POJO.Post
import com.example.flex.R
import com.example.flex.Requests.PhotoRequests

class PostsAccountAdapter(val fragment: Fragment) :
    RecyclerView.Adapter<PostsAccountAdapter.PostsViewHolder>() {
    private val posts = mutableListOf<Post>()

    class PostsViewHolder(private val v: View, private val fragment: Fragment) :
        RecyclerView.ViewHolder(v) {
        private val mainUserAvatar: ImageView = v.findViewById(R.id.user_icon)
        private val mainUserName: TextView = v.findViewById(R.id.user_name)
        private val postImage: ImageView = v.findViewById(R.id.main_image)
        private val fire: TextView = v.findViewById(R.id.fire_icon)
        private val firesCount: TextView = v.findViewById(R.id.fire_count)
        private val commentsCount: TextView = v.findViewById(R.id.comments_count)
        private val shareCount: TextView = v.findViewById(R.id.share_count)
        private val postText: TextView = v.findViewById(R.id.post_text)

        init {
            postImage.setOnClickListener {

            }
            like(fire)
        }

        private fun like(textView: TextView): View.OnClickListener =
            View.OnClickListener {
                textView.setText(textView.text.toString().toInt() + 1)
                textView.setTextColor(Color.RED)
                textView.setOnClickListener(dislike(fire))
            }

        private fun dislike(textView: TextView): View.OnClickListener =
            View.OnClickListener {
                textView.setText(textView.text.toString().toInt() + 1)
                textView.setTextColor(Color.BLACK)
                textView.setOnClickListener(like(fire))
            }

        fun bind(post: Post) {
            if (post.imageUrl != "") {
                val sharedPreferences =
                    fragment.context!!.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
                val request = PhotoRequests(
                    fragment,
                    sharedPreferences.getString(MainData.CRSFTOKEN, ""),
                    sharedPreferences.getString(MainData.SESION_ID, "")
                )
                request.downloadPhotoByUrl(post.imageUrl, postImage)
                request.downloadPhotoByUrl(post.mainUser.imageUrl, mainUserAvatar)
                mainUserName.text = post.mainUser.name
                commentsCount.text = post.countOfComments.toString()
                postText.text = post.postText
                firesCount.text=post.countOfFires.toString()
                shareCount.text=post.countOfShares.toString()
            }
        }
    }

    fun addPosts(list: List<Post>) {
        posts.addAll(list)
        notifyDataSetChanged()
    }

    fun setPosts(list: List<Post>) {
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
        return PostsViewHolder(v, fragment)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        holder.bind(posts[position])
    }

}