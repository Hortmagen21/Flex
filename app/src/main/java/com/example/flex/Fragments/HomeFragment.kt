package com.example.flex.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.Adapter.PostAdapter
import com.example.flex.POJO.Comment
import com.example.flex.POJO.Post
import com.example.flex.POJO.User
import com.example.flex.R

class HomeFragment : Fragment(), PostAdapter.OnUserClickListener {
    lateinit var v: View
    lateinit var recycler: RecyclerView
    lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_home, container, false)
        loadRecyclerView()
        loadPosts()
        return v
    }

    private fun getPosts(): Collection<Post> {
        return listOf(
            Post(
                1,
                5,
                654,
                95,
                User(
                    1,
                    "Vlad",
                    "https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80",
                    325,
                    235
                ),
                "https://static.toiimg.com/photo/72975551.cms",
                Comment(
                    User(
                        2,
                        "DeLitX",
                        "https://images.pexels.com/photos/34950/pexels-photo.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                        235,
                        523
                    ), "sdfs"
                ),
                "top content,ya perviy"
            ),
            Post(
                2,
                651,
                64,
                5,
                User(
                    3,
                    "Vilard",
                    "https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg",
                    467,
                    26
                ),
                "https://www.freedigitalphotos.net/images/img/homepage/394230.jpg",
                Comment(
                    User(
                        4,
                        "Hortmagen",
                        "https://media.gettyimages.com/photos/beautiful-book-picture-id865109088?s=612x612",
                        6236,
                        2636
                    ), "sddgsdigjofpdgsghsdgh"
                ),
                "Проверка кириллицы"
            ),
            Post(
                3,
                65,
                6984,
                25,
                User(
                    5,
                    "Max",
                    "https://media3.s-nbcnews.com/j/newscms/2019_41/3047866/191010-japan-stalker-mc-1121_06b4c20bbf96a51dc8663f334404a899.fit-760w.JPG",
                    236,
                    46
                ),
                "https://images.unsplash.com/photo-1503803548695-c2a7b4a5b875?ixlib=rb-1.2.1&w=1000&q=80",
                Comment(User(6, "", "", 2362, 237), ""),
                ""
            )
        )
    }

    private fun loadPosts() {
        postAdapter.addItems(getPosts())
    }

    private fun loadRecyclerView() {
        recycler = v.findViewById(R.id.main_recycler_view)
        recycler.layoutManager = LinearLayoutManager(v.context)

        postAdapter = PostAdapter(this)
        recycler.adapter = postAdapter
    }

    override fun onUserClick(user: User) {
        val fragment = AccountFragment()
        fragmentManager!!.beginTransaction().replace(R.id.frame_container, fragment, "fragment_tag")
            .commit()
        fragment.setUser(user)
    }
}