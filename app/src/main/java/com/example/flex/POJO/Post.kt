package com.example.flex.POJO

data class Post(
    val id: Long,
    val mainUser: User=User(0,""),
    val imageUrl: String,
    val postText: String,
    val date:Long=0,
    val countOfFires: Long=0,
    val countOfComments: Long=0,
    val countOfShares: Long=0,
    val comment: Comment?=null
)