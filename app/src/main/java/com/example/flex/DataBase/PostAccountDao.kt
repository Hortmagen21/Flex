package com.example.flex.DataBase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.flex.Adapter.PhotosAdapter
import com.example.flex.POJO.PostAccount

@Dao
interface PostAccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts:List<PostAccount>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post:PostAccount)
    @Update
    fun updateAll(posts: List<PostAccount>)
    @Transaction
    @Query("delete from posts_account_database")
    fun deleteAllPosts()
    @Query("select * from posts_account_database order by id desc")
    fun getAllPosts():LiveData<List<PostAccount>>
    @Query("select * from posts_account_database where belongs=:userId order by id desc")
    fun getAllPostsOfUser(userId:Long):LiveData<List<PostAccount>>
    @Query("select * from posts_account_database where id=:id" )
    fun getPost(id:Long):PostAccount
    @Query("delete from posts_account_database where belongs=:id")
    fun deleteAllFromUser(id:Long)
}