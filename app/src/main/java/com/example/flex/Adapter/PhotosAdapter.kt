package com.example.flex.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.POJO.PostAccount
import com.example.flex.R

class PhotosAdapter(val downloadPhoto:PhotosDownload) : RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {
    private val photos = mutableListOf<PostAccount>()

    class PhotosViewHolder(private val v: View,private val mDownloadPhoto:PhotosDownload) : RecyclerView.ViewHolder(v) {
        private val photo = v.findViewById<ImageView>(R.id.image_item)

        init {
            photo.setOnClickListener {

            }
        }

        fun bind(image: PostAccount) {
            if (image.imageUrlMini != "") {
                mDownloadPhoto.downloadPhoto(image.imageUrlMini,photo)
            }
        }
    }

    fun addPhotos(list: List<PostAccount>) {
        photos.addAll(list)
        notifyDataSetChanged()
    }

    fun setPhotos(list: List<PostAccount>) {
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
        return PhotosViewHolder(v,downloadPhoto)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.bind(photos[position])
    }
    interface PhotosDownload{
        fun downloadPhoto(link:String,photo:ImageView)
    }

    interface onPhotosClickListener {
        fun onPhotoClick()
    }
}