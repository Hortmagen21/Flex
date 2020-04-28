package com.example.flex.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.POJO.Comment
import com.example.flex.POJO.Post
import com.example.flex.POJO.User
import com.example.flex.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentsAdapter(
    private val mMainPostId: Long,
    private val mCommentInterface: CommentInterface
) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {
    private val mListOfComments = mutableListOf<Comment>()

    class CommentsViewHolder(private val v: View, private val mCommentInterface: CommentInterface) :
        RecyclerView.ViewHolder(v) {
        private val mUserAvatar = v.findViewById<ImageView>(R.id.user_comment_icon)
        private val mUserName = v.findViewById<TextView>(R.id.user_comment_name)
        private val mCommentText = v.findViewById<TextView>(R.id.comment_text)

        init {
            mUserAvatar.setOnClickListener {

            }
        }

        fun bind(comment: Comment) {
            if (comment.user != User(comment.userId)) {
                mCommentInterface.downloadPhoto(comment.userAvatarLink, mUserAvatar)
                mUserName.text = comment.userName
            }
            mCommentText.text = comment.text
            CoroutineScope(IO).launch {
                val user = mCommentInterface.getUserById(comment.userId)
                withContext(Main) {
                    mUserName.text = user.name
                    mCommentInterface.downloadPhoto(user.imageUrl, mUserAvatar)
                }
            }
        }
    }

    interface CommentInterface {
        fun downloadPhoto(link: String, photo: ImageView)
        suspend fun getUserById(userId: Long): User
    }

    fun setComments(comments: List<Comment>) {
        mListOfComments.clear()
        mListOfComments += comments
        notifyDataSetChanged()
    }

    fun addComments(comments: List<Comment>) {
        mListOfComments += comments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.comment_fragment, parent, false)
        return CommentsViewHolder(v, mCommentInterface)
    }

    override fun getItemCount(): Int {
        return mListOfComments.size
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.bind(mListOfComments[position])
    }
}