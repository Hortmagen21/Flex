package com.example.flex.DataBase

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flex.POJO.Comment
import com.example.flex.POJO.Post
import com.example.flex.POJO.PostAccount
import com.example.flex.POJO.User

@Database(entities = [Post::class, PostAccount::class, User::class,Comment::class], version = 1)
abstract class PostDatabase : RoomDatabase() {
    companion object {
        private var mInstance: PostDatabase? = null
        fun get(application: Application): PostDatabase {
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(
                    application, PostDatabase::class.java, "posts_database"
                ).build()
            }
            return mInstance!!
        }
    }

    abstract fun getPostDao(): PostDao
    abstract fun getPostAccountDao(): PostAccountDao
    abstract fun getUserDao(): UserDao
    abstract fun getCommentDao():CommentDao

    //TODO add comments and interaction with comments
}