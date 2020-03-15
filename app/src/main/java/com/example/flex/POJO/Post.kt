package com.example.flex.POJO

data class Post(
        val id: Long,
        val countOfFires: Long,
        val countOfComments: Long,
        val countOfShares: Long,
        val mainUser: User,
        val imageUrl: String,
        val comment:Comment,
        val postText: String
)