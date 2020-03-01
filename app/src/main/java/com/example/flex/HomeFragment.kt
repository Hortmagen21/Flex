package com.example.flex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {
    lateinit var button:Button
    lateinit var button2:Button
    lateinit var v:View
    lateinit var recycler:RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v=inflater.inflate(R.layout.fragment_home,container,false)
        var fragment1=PublishedPhotoFragment("https://photos8.com/wp-content/uploads/2019/11/Love-Hands-Heart-Copyright-by-Sam-Mugraby.jpg",
            "https://s23527.pcdn.co/wp-content/uploads/2019/02/moon-1-745x517.jpg.optimal.jpg",
            "https://images.pexels.com/photos/1820567/pexels-photo-1820567.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500")
        fragmentManager!!.beginTransaction().replace(R.id.fragment1,fragment1,"fragment1 tag").commit()
        var fragment2=PublishedPhotoFragment("https://s23527.pcdn.co/wp-content/uploads/2019/12/Downside-Up-745x449.jpg.optimal.jpg",
            "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg",
            "https://images.pexels.com/photos/2873992/pexels-photo-2873992.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500")
        fragmentManager!!.beginTransaction().replace(R.id.fragment2,fragment2,"fragment2 tag").commit()
        var fragment3=PublishedPhotoFragment("https://www.designer.io/wp-content/uploads/2019/10/1-1024x698.png",
            "https://media.istockphoto.com/photos/heart-shape-paper-book-on-the-beach-picture-id817147678?k=6&m=817147678&s=612x612&w=0&h=vNJJJoRPaieom61bMwQxaGtOImMhJsoxz1OIgLZNOEw=",
            "https://www.androidpolice.com/wp-content/uploads/2019/10/Google-Photos-Colorize-Beta.png")
        fragmentManager!!.beginTransaction().replace(R.id.fragment3,fragment3,"fragment3 tag").commit()
        var fragment4=PublishedPhotoFragment("https://www.slrlounge.com/wp-content/uploads/2020/01/Dirk-Dallas-Drone-Photo-splash-image-wppi-expo.jpg",
            "https://dynaimage.cdn.cnn.com/cnn/q_auto,h_600/https%3A%2F%2Fcdn.cnn.com%2Fcnnnext%2Fdam%2Fassets%2F200109201648-28-week-in-photos-0110.jpg",
            "https://shotkit.com/wp-content/uploads/2019/10/tips-for-unique-travel-photos.jpg")
        fragmentManager!!.beginTransaction().replace(R.id.fragment4,fragment4,"fragment4 tag").commit()
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
        return v
    }
}