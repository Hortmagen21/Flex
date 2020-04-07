package com.example.flex.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.MainData
import com.example.flex.POJO.Post
import com.example.flex.R
import com.example.flex.Requests.PhotoRequests
import com.squareup.picasso.Picasso

class PhotosAdapter(val fragment: Fragment) : RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {
    private val photos = mutableListOf<Post>()

    class PhotosViewHolder(private val v: View,private val fragment:Fragment) : RecyclerView.ViewHolder(v) {
        private val photo = v.findViewById<ImageView>(R.id.image_item)

        init {
            photo.setOnClickListener {

            }
        }

        fun bind(image: Post) {
            if (image.imageUrl != "") {
                val sharedPreferences=fragment.context!!.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
                val request=PhotoRequests(fragment,
                    sharedPreferences.getString(MainData.CRSFTOKEN,""),
                sharedPreferences.getString(MainData.SESION_ID,""))
                request.downloadPhotoByUrl(image.imageUrl,photo)
            }
        }
    }

    fun addPhotos(list: List<Post>) {
        photos.addAll(list)
        notifyDataSetChanged()
    }

    fun setPhotos(list: List<Post>) {
        photos.clear()
        photos.addAll(list)
        notifyDataSetChanged()
    }

    fun clearPhotos() {
        photos.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.image_item, parent, false)
        return PhotosViewHolder(v,fragment)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    interface onPhotosClickListener {
        fun onPhotoClick()
    }
}