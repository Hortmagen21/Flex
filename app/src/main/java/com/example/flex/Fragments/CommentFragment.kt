package com.example.flex.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.flex.POJO.Comment
import com.example.flex.R
import com.squareup.picasso.Picasso

class CommentFragment(
    private val comment: Comment
) : Fragment() {
    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.comment_fragment, container, false)
        setComment()
        addActionListener()
        return v
    }

    private fun setComment() {
        val photo = v.findViewById<ImageView>(R.id.user_comment_icon)
        val name = v.findViewById<TextView>(R.id.user_comment_name)
        val text = v.findViewById<TextView>(R.id.comment_text)
        Picasso.get().load(comment.user.imageUrl).into(photo)
        name.text = comment.user.name
        text.text = comment.text
    }

    private fun addActionListener() {
        val photo = v.findViewById<ImageView>(R.id.user_comment_icon)
        photo.setOnClickListener {
        }
    }


    fun addThisComment(id: Int) {
        fragmentManager!!.beginTransaction().replace(id, this).commit()
    }
}