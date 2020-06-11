package com.example.flex.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.POJO.User
import com.example.flex.R
import com.squareup.picasso.Picasso

class SearchAdapter(private var onUserClickListener: OnUserClickListener) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    private var searchList = mutableListOf<User>()

    class SearchViewHolder(
        private val v: View,
        private val onUserClickListener: OnUserClickListener
    ) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private val userIcon: ImageView = v.findViewById(R.id.search_user_icon)
        private val username: TextView = v.findViewById(R.id.search_username)
        lateinit var currentUser: User

        init {
            v.setOnClickListener(this)
        }

        fun bind(user: User) {
            currentUser = user
            username.text = user.name
            if (user.imageUrl != "") Picasso.get().load(user.imageUrl).into(userIcon)
            else userIcon.setImageResource(R.drawable.ic_launcher_background)
        }

        override fun onClick(v: View?) {
            onUserClickListener.onUserClick(currentUser)
        }
    }

    interface OnUserClickListener {
        fun onUserClick(user: User)
    }

    fun setUsers(users: List<User>?) {
        searchList.clear()
        if (users != null) {
            searchList.addAll(users)
        }
        notifyDataSetChanged()
    }

    fun addUsers(users: List<User>?) {
        if (users != null) {
            searchList.addAll(users)
        }
        notifyDataSetChanged()
    }

    fun clearUsers() {
        searchList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.search_user, parent, false)
        return SearchViewHolder(view, onUserClickListener)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(searchList[position])
    }
}