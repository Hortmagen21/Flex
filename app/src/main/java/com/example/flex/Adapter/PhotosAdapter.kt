package com.example.flex.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.R
import com.squareup.picasso.Picasso

class PhotosAdapter : RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {
    private val photos = mutableListOf<String>()

    class PhotosViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val photo = v.findViewById<ImageView>(R.id.image_item)

        init {
            photo.setOnClickListener {

            }
        }

        fun bind(image: String) {
            if (image != "") {
                Picasso.get().load(image).into(photo)
            }
        }
    }

    fun addPhotos(list: List<String>) {
        photos.addAll(list)
        notifyDataSetChanged()
    }

    fun setPhotos(list: List<String>) {
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
        return PhotosViewHolder(v)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    interface onPhotosClickListener {
        fun onPhotoClick()
    }
}