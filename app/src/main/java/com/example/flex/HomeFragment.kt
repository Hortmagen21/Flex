package com.example.flex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.Adapter.PostAdapter
import com.example.flex.POJO.Post
import com.example.flex.POJO.User
import java.util.*

class HomeFragment : Fragment() {
    lateinit var button:Button
    lateinit var button2:Button
    lateinit var v:View
    lateinit var recycler:RecyclerView
    lateinit var postAdapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v=inflater.inflate(R.layout.fragment_home,container,false)

        /*recycler=v.findViewById<RecyclerView>(R.id.recycler).apply {
            layoutManager=LinearLayoutManager(this.context)
        }*/
        /*button=v.findViewById(R.id.button)
        button.setOnClickListener {
            startActivity(Intent(v.context,Registration::class.java))
        }
        button2=v.findViewById(R.id.button2)
        button2.setOnClickListener {
            startActivity(Intent(v.context,SignIn::class.java))
        }*/
        loadRecyclerView()
        loadPosts()
        return v
    }
    fun getPosts():Collection<Post>{
        return Arrays.asList(
            Post(1,5,654,95, User(1,"Vlad","https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"),
                "https://static.toiimg.com/photo/72975551.cms",
                User(2,"DeLitX","https://images.pexels.com/photos/34950/pexels-photo.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500"),
                "top content,ya perviy"),
            Post(2,651,64,5, User(3,"Vilard","https://cdn.pixabay.com/photo/2013/07/21/13/00/rose-165819__340.jpg"),
                "https://www.freedigitalphotos.net/images/img/homepage/394230.jpg",
                User(4,"Hortmagen","https://media.gettyimages.com/photos/beautiful-book-picture-id865109088?s=612x612"),
                "Проверка кириллицы"),
            Post(3,65,6984,25, User(5,"Max","https://media3.s-nbcnews.com/j/newscms/2019_41/3047866/191010-japan-stalker-mc-1121_06b4c20bbf96a51dc8663f334404a899.fit-760w.JPG"),
                "https://images.unsplash.com/photo-1503803548695-c2a7b4a5b875?ixlib=rb-1.2.1&w=1000&q=80",
                User(6,"Dima","https://images.pexels.com/photos/414612/pexels-photo-414612.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"),
                "fu,skatilsya")
        )
    }
    fun loadPosts(){
        postAdapter.addItems(getPosts())
    }
    fun loadRecyclerView(){
        recycler=v.findViewById(R.id.main_recycler_view)
        recycler.layoutManager=LinearLayoutManager(v.context)

        postAdapter=PostAdapter()
        recycler.adapter=postAdapter
    }
}