package com.example.flex.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.POJO.Post
import com.example.flex.R
import com.squareup.picasso.Picasso

class PostAdapter: RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private var postList= mutableListOf<Post>()
    class PostViewHolder:RecyclerView.ViewHolder{
        var mainUserAvatar:ImageView
        var mainUserName:TextView
        var postImage:ImageView
        var commentatorAvatar:ImageView
        var commentatorName:TextView
        var commentText:TextView
        var firesCount:TextView
        var commentsCount:TextView
        var shareCount:TextView
        constructor(v: View):super(v){
            mainUserAvatar=v.findViewById(R.id.user_icon)
            mainUserName=v.findViewById(R.id.user_name)
            postImage=v.findViewById(R.id.main_image)
            commentatorAvatar=v.findViewById(R.id.user_comment_icon)
            commentatorName=v.findViewById(R.id.user_comment_name)
            commentText=v.findViewById(R.id.comment_text)
            firesCount=v.findViewById(R.id.fire_count)
            commentsCount=v.findViewById(R.id.comments_count)
            shareCount=v.findViewById(R.id.share_count)
        }
        public fun bind (post:Post){
            mainUserName.text=post.mainUser.name
            Picasso.get().load(post.mainUser.imageUrl).into(mainUserAvatar)
            Picasso.get().load(post.imageUrl).into(postImage)
            Picasso.get().load(post.commentUser.imageUrl).into(commentatorAvatar)
            commentatorName.text=post.commentUser.name
            commentText.text=post.commentText
            firesCount.text=post.countOfFires.toString()
            commentsCount.text=post.countOfComments.toString()
            shareCount.text=post.countOfShares.toString()
        }
    }
    fun addItems(posts:Collection<Post>){
        postList.addAll(posts)
        notifyDataSetChanged()
    }
    fun clearItems(){
        postList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.published_photo_layout,parent,false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(postList.get(position))
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}