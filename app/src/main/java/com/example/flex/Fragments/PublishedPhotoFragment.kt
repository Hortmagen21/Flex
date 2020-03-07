package com.example.flex.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.flex.R
import com.squareup.picasso.Picasso

class PublishedPhotoFragment:Fragment {
    lateinit var v:View
    lateinit var photo:ImageView
    lateinit var avatar:ImageView
    lateinit var commentatorAvatar:ImageView
    lateinit var photoPath: String
    lateinit var iconPath: String
    lateinit var commentIconPath: String

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        v=inflater.inflate(R.layout.published_photo_layout,container,false)
        setPhotos(photoPath,iconPath,commentIconPath)
        return v
    }
    fun setPhotos(photoPath: String,iconPath:String,commentIconPath: String){
        photo=v.findViewById(R.id.main_image)
        avatar=v.findViewById(R.id.user_icon)
        commentatorAvatar=v.findViewById(R.id.user_comment_icon)
        Picasso.get().load(photoPath).into(photo)
        Picasso.get().load(iconPath).into(avatar)
        Picasso.get().load(commentIconPath).into(commentatorAvatar)
    }
    constructor(photoPath: String,iconPath: String,commentIconPath: String){
        this.photoPath=photoPath
        this.iconPath=iconPath
        this.commentIconPath=commentIconPath
    }
}