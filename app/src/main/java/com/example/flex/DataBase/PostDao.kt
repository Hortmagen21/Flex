package com.example.flex.DataBase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.flex.POJO.Post

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post:Post)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts:List<Post>)
    @Delete
    fun delete(post: Post)
    @Update
    fun update(post: Post)
    @Query("delete from post_database")
    fun deleteAllPosts()
    @Query("select * from post_database order by id desc")
    fun getSortedPosts():LiveData<List<Post>>
    @Query("select * from post_database where id=:postId")
    fun getPostById(postId:Long):Post
}